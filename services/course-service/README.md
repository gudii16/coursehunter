# Course Service

Manages the course catalog. Implements CQRS — writes go through a command handler, reads are served from a denormalized query model.

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/courses/hi` | Health check greeting |

## Tech Stack

- Spring Boot 3.4 / Java 21
- Spring Web (REST)
- Spring Data JPA + PostgreSQL (write model)
- Spring Cloud Stream + Kafka (event publishing/consuming)

## Run Locally

```bash
# From the services/ directory
mvn spring-boot:run -pl course-service
```

Service starts on **port 8082**.

## Test

```bash
mvn test -pl course-service
```

## Structure

```
course-service/
├── src/main/java/com/coursehunter/course/
│   ├── CourseServiceApplication.java
│   └── controller/
│       └── CourseController.java
├── src/main/resources/
│   └── application.yml
└── src/test/java/com/coursehunter/course/
    └── controller/
        └── CourseControllerTest.java
```
