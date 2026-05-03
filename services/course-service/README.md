# Course Service

Manages the course catalog for the CourseHunter platform. Responsible for creating, updating, publishing, and archiving courses — and keeping a fast read model in sync via Kafka events.

---

## Design Patterns

### 1. CQRS — Command Query Responsibility Segregation

**What it is**: Separates the write path (commands) from the read path (queries) into distinct models, services, and data stores.

**Why here**: Course listing is read-heavy — students query the catalog far more often than instructors create or update courses. Mixing reads and writes in the same model means every read query pays the cost of joins across `courses`, `tags`, and `course_tags`. CQRS lets each side scale and evolve independently.

```
Write side (Command)                Read side (Query)
────────────────────                ─────────────────
CourseCommandService                CourseQueryService
CourseRepository                    CourseCatalogViewRepository
courses table (normalized)          course_catalog_view (denormalized)
  + tags                              tags already embedded as array
  + course_tags
```

**Package layout:**
```
command/
├── dto/       CreateCourseRequest, UpdateCourseRequest, CourseResponse
├── entity/    Course, Tag, CourseStatus
├── event/     CourseEventPublisher
├── repository/CourseRepository, TagRepository
└── service/   CourseCommandService

query/
├── dto/       CourseSummary
├── entity/    CourseCatalogView
├── projector/ CourseCatalogProjector
├── repository/CourseCatalogViewRepository
└── service/   CourseQueryService
```

---

### 2. Event-Driven Projection (Event Sourcing lite)

**What it is**: The read model (`course_catalog_view`) is never written directly by the API. It is populated and kept in sync by a Kafka consumer (the Projector) that reacts to domain events emitted by the command side.

**Why here**: Dual-writing to both tables inside the same transaction creates tight coupling and risks inconsistency if the second write fails. Kafka acts as a reliable, replayable bridge — if the projector crashes, it replays from its last committed offset and catches up automatically.

```
POST /api/courses
  │
  ├─→ courses table          (write model — source of truth)
  └─→ CourseEvent (CREATED)  → Kafka: course.events
                                      │
                              CourseCatalogProjector
                                      │
                              course_catalog_view    (read model — query optimized)
```

---

### 3. Envelope Pattern

**What it is**: Instead of one Kafka topic per event type, all course domain events are published to a single topic `course.events` wrapped in a `CourseEvent` envelope that carries an `eventType` discriminator field.

**Why here**: Three separate topics (`course.created`, `course.updated`, `course.deleted`) would require three separate consumers, three deserializer configs, and three Kafka bindings. The envelope keeps the YAML config minimal and the projector logic in one place.

```json
{
  "eventType": "CREATED",
  "id": "uuid",
  "title": "Spring Boot Mastery",
  "status": "DRAFT",
  "totalSeats": 30,
  "tags": ["java", "spring-boot"]
}
```

The projector routes internally via a `switch` on `eventType`:
```java
switch (event.getEventType()) {
    case CREATED → onCreate(event);
    case UPDATED → onUpdate(event);
    case DELETED → onDelete(event);
}
```

---

### 4. Repository Pattern

**What it is**: Data access is abstracted behind Spring Data JPA repository interfaces. Services never write SQL directly.

**Why here**: Keeps business logic in services decoupled from persistence. Makes it easy to test services by mocking repositories. The read model repository (`CourseCatalogViewRepository`) also carries native Postgres-specific queries (array containment `@>`, `ILIKE`) without leaking SQL into the service layer.

```java
// Tag search using Postgres GIN index
@Query(value = "SELECT * FROM course_inventory.course_catalog_view WHERE :tag = ANY(tags)", nativeQuery = true)
List<CourseCatalogView> findByTag(@Param("tag") String tag);
```

---

### 5. Domain Events via StreamBridge

**What it is**: After each successful write, `CourseCommandService` publishes a `CourseEvent` via `StreamBridge` (Spring Cloud Stream). The publisher is a thin wrapper — the service doesn't know about Kafka directly.

**Why `StreamBridge` over `KafkaTemplate`**: The service should not be coupled to Kafka. `StreamBridge` sends to a **binding name**, not a topic name. The actual topic (`course.events`) is configured in `application.yml`. If the broker ever changes to RabbitMQ, only the binder dependency and YAML change — zero code changes.

---

### 6. `@Transactional` on Command Operations

**What it is**: Every write method in `CourseCommandService` is wrapped in a database transaction.

**Why here**: `create()` touches three tables — `courses`, `tags`, and `course_tags`. Without a transaction, a partial failure (e.g. tag insert fails) leaves orphaned rows. `@Transactional` ensures all-or-nothing: either everything commits or everything rolls back.

`update()` additionally relies on Hibernate's **dirty checking** — the entity loaded via `findById()` is "managed" within the transaction, so Hibernate auto-detects changed fields and generates a minimal `UPDATE` SQL at commit time.

---

### 7. Many-to-Many with `HashSet`

**What it is**: The `Course` ↔ `Tag` relationship is modeled as `@ManyToMany` using a `HashSet<Tag>`.

**Why `HashSet` over `List`**: Hibernate has a known issue with `@ManyToMany` + `List` — on any collection change it deletes all rows in the join table and re-inserts them. `HashSet` avoids this by using set-identity semantics, resulting in minimal `INSERT`/`DELETE` on `course_tags`.

Tags are also **reused across courses** — `resolveTags()` looks up by slug first and only inserts if the tag doesn't exist yet, avoiding duplicate tag rows.

---

## Data Model

```
courses (write model)
  └── course_tags (join)
        └── tags

course_catalog_view (read model — denormalized)
  tags stored as VARCHAR[] array for fast GIN-indexed search

seat_inventory    (1:1 with courses)
seat_reservations (1:many with courses — open issue, TTL-based soft holds)
```

---

## Endpoints

| Method | Path | Side | Description |
|---|---|---|---|
| `GET` | `/api/courses` | Query | List all — supports `?tag=`, `?status=`, `?search=` |
| `GET` | `/api/courses/{id}` | Query | Get by ID |
| `POST` | `/api/courses` | Command | Create course |
| `PUT` | `/api/courses/{id}` | Command | Update course |
| `PATCH` | `/api/courses/{id}/publish` | Command | Set status PUBLISHED |
| `PATCH` | `/api/courses/{id}/archive` | Command | Set status ARCHIVED |
| `DELETE` | `/api/courses/{id}` | Command | Delete course |

---

## Kafka

| Topic | Direction | Event Types |
|---|---|---|
| `course.events` | Outbound (publish) | `CREATED`, `UPDATED`, `DELETED` |
| `course.events` | Inbound (consume) | `CREATED`, `UPDATED`, `DELETED` |

Consumer group: `course-catalog-projector`

---

## Postman Collection

A ready-to-use Postman collection is available at:
```
configs/postman/course-service.postman_collection.json
```

**Import steps:**
1. Open Postman → click **Import** → select the file above
2. The collection has two folders: **Queries** and **Commands**
3. `baseUrl` defaults to `http://localhost:8082` — change it in **Collection Variables** if needed

**Auto-capture of `courseId`:** The **Create Course** request has a test script that automatically extracts the `id` from the response and saves it to the `{{courseId}}` collection variable. All subsequent requests (Get by ID, Update, Publish, Archive, Delete) use `{{courseId}}` — no manual copy-paste needed.

**Suggested run order:**
1. Create Course → captures `{{courseId}}`
2. Get All Courses
3. Get Course By ID
4. Filter By Tag / Status / Search
5. Update Course
6. Publish Course
7. Archive Course
8. Delete Course

---

## Run Locally

```bash
# From services/ directory
mvn spring-boot:run -pl course-service

# Requires:
# - Postgres running on localhost:5432  (make run)
# - Kafka running on localhost:9092     (make run)
```

Service starts on **port 8082**.

## Test

```bash
mvn test -pl course-service
```
