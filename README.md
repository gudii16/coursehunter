# coursehunter

Act as a Principal Solutions Architect and Enterprise Cloud Engineer. Generate an enterprise-grade, advanced microservices project blueprint for an Online Learning Management Platform (e.g., CampusConnect).

The project must demonstrate advanced enterprise patterns, fault tolerance, and event-driven architecture with secure, containerized deployment on Kubernetes using the following tech stack:

Framework: Spring Boot 3.4+ / Java 21+
Security: Spring Security 6.x (OAuth2 Resource Server with JWT validation)
Advanced Patterns: Saga pattern (for student enrollment and payment processing), CQRS (for course catalog/gradebook read models), and Circuit Breaker
Inter-service Communication: Synchronous REST (RestClient/WebClient with JWT propagation) and Asynchronous Event-driven messaging (Kafka/RabbitMQ via Spring Cloud Stream to trigger student notification and grading events)
Observability & Tracing: Distributed tracing with OpenTelemetry and Micrometer
Containerization: Distroless or multi-stage Docker builds
Deployment/Infrastructure: Kubernetes manifests (Deployment, Service, Ingress) or Helm charts
Please provide the following:

Architecture Design & Enterprise Patterns
Describe the architecture and explain the specific usage of the Saga, CQRS, and Circuit Breaker patterns within this educational ecosystem (e.g., how to handle an enrollment process that involves the Payment Service and Course Catalog Service).
Project Structure
Provide a visual directory tree structure of the repository, highlighting a multi-module or multi-repository layout.
Core Code Implementation
Provide production-ready Spring Boot code (with imports and annotations) for:
Security Configuration using Spring Security 6 to decode and validate JWT tokens at the API Gateway level.
Resilience4j Circuit Breaker/Retry configuration on an inter-service client (e.g., Student Service calling the Course Service).
An asynchronous event publisher and listener example using Spring Cloud Stream with Kafka (e.g., publishing an EnrollmentCompletedEvent).
Containerization Files
Provide a multi-stage Dockerfile or a distroless-based Dockerfile for one of the microservices to optimize the final image size.
Infrastructure & Orchestration
Write the Kubernetes YAML configurations: Deployment, Service, ConfigMap, and Ingress to expose the API Gateway.

Below is an enterprise-grade, production-aligned microservices blueprint for a cloud-native Online Learning Platform — “CampusConnect”. It integrates best practices for scalability, resilience, observability, and security across a Kubernetes-deployed, event-driven ecosystem.

1. Architecture Design & Enterprise Patterns
   High-Level Architecture Overview
   The platform consists of several microservices collaborating via REST and Kafka topics:

API Gateway — Auth entry point (JWT validation + routing)
Identity Service — Manages OAuth2 / JWT token issuing
Student Service — Handles student profiles and enrollment orchestration (Saga initiator)
Course Service — Manages course catalog and seat availability (CQRS read/write separation)
Payment Service — Processes payments and emits compensation events if needed
Notification Service — Listens to Kafka events and sends email/SMS notifications
Gradebook Service — Updates grades asynchronously (CQRS model)
Audit Service — Records key events for compliance (event listener)
All services communicate via:

Synchronous REST (for real-time queries)
Asynchronous Kafka topics (for domain events between decoupled systems)
Diagram (conceptually):



              +----------------------+
              |     API Gateway      |
              | (JWT/OAuth2 + Routing)|
              +----------+-----------+
                         |
           +-------------+---------------+
           |                             |
    +------+-------+             +-------+------+
    | Student Svc  | ----REST----| Course Svc   |
    | (Saga Orches)|             | (CQRS Model) |
    +------+-------+             +-------+------+
           |                             |
     Kafka |                             | Kafka
           v                             v
    +-------------+               +--------------+
    | Payment Svc | <---> Kafka -->| Notification |
    | (Saga Step) |               |  & Gradebook |
    +-------------+               +--------------+
Enterprise Patterns Used
1. Saga Pattern – Enrollment Process
   Use choreography-based Saga for distributed enrollment and payment:

Student Service initiates enrollment (publishes EnrollmentCreatedEvent).
Payment Service listens, processes payment → emits PaymentCompletedEvent.
Course Service reserves a seat → emits SeatAllocatedEvent.
Student Service listens for all success signals → marks enrollment COMPLETED.
On failure (e.g., payment decline), compensating transactions reverse partial steps — payment refunded, seat released.
2. CQRS – Course Catalog & Gradebook
   CourseCatalogWriteModel handles CRUD, applying business logic.
   CourseCatalogReadModel stores denormalized views for fast read (Elasticsearch or Postgres read replica).
   Gradebook follows same pattern: write via events, read via query microservice.
3. Circuit Breaker – Inter-service Resilience
   When Student Service calls Course Service (e.g., for seat availability), a Resilience4j CircuitBreaker and Retry pattern ensures degraded service instead of total failure.

2. Project Structure (Multi-module Monorepo)


campusconnect/
├── gateway-service/
│   ├── src/main/java/com/campusconnect/gateway/
│   │   └── security/
│   │       └── GatewaySecurityConfig.java
│   └── Dockerfile
├── student-service/
│   ├── src/main/java/com/campusconnect/student/
│   │   ├── saga/
│   │   ├── event/
│   │   │   ├── EnrollmentCreatedEvent.java
│   │   │   ├── EnrollmentCompletedEvent.java
│   │   └── service/
│   └── Dockerfile
├── course-service/
│   ├── src/main/java/com/campusconnect/course/
│   │   ├── command/        # Write model
│   │   ├── query/          # Read model
│   │   └── config/
│   └── Dockerfile
├── payment-service/
│   ├── src/main/java/com/campusconnect/payment/
│   └── Dockerfile
├── notification-service/
│   └── src/main/java/com/campusconnect/notification/
│       └── listener/
│           └── EnrollmentCompletedListener.java
├── common-library/
│   └── src/main/java/com/campusconnect/common/
│       ├── dto/
│       ├── event/
│       └── config/
└── kubernetes/
├── gateway-deployment.yaml
├── gateway-service.yaml
├── gateway-ingress.yaml
└── configmap.yaml
3. Core Code Implementation
   a. Spring Security 6 Gateway JWT Configuration
   java


package com.campusconnect.gateway.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
@Configuration
public class GatewaySecurityConfig {
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http
.csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth
.requestMatchers("/actuator/**").permitAll()
.requestMatchers("/api/public/**").permitAll()
.anyRequest().authenticated()
)
.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
return http.build();
}
}
application.yml (gateway):
yaml


spring:
security:
oauth2:
resourceserver:
jwt:
issuer-uri: [auth-campusconnect.example.com](https://auth-campusconnect.example.com/realms/campusconnect)
jwk-set-uri: [auth-campusconnect.example.com](https://auth-campusconnect.example.com/realms/campusconnect/protocol/openid-connect/certs)
b. Resilience4j Circuit Breaker + Retry Example
java


package com.campusconnect.student.client;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Service
public class CourseClient {
private final WebClient webClient;
public CourseClient(WebClient.Builder builder) {
this.webClient = builder.baseUrl("[course-service](http://course-service)").build();
}
@CircuitBreaker(name = "courseServiceCircuit", fallbackMethod = "fallbackGetCourse")
@Retry(name = "courseServiceRetry")
public Mono<CourseDto> getCourseById(String courseId) {
return webClient.get()
.uri("/api/courses/{id}", courseId)
.retrieve()
.bodyToMono(CourseDto.class);
}
private Mono<CourseDto> fallbackGetCourse(String courseId, Throwable t) {
CourseDto cached = new CourseDto(courseId, "Unavailable", "Course temporarily unreachable");
return Mono.just(cached);
}
}
application.yml (resilience config)
yaml


resilience4j:
circuitbreaker:
instances:
courseServiceCircuit:
slidingWindowSize: 10
failureRateThreshold: 50
waitDurationInOpenState: 10s
retry:
instances:
courseServiceRetry:
maxAttempts: 3
waitDuration: 2s
c. Asynchronous Event Publisher and Listener (Kafka)
Publisher Example – EnrollmentCompletedEvent

java


package com.campusconnect.student.event;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
@Service
public class EnrollmentEventPublisher {
private final StreamBridge streamBridge;
public EnrollmentEventPublisher(StreamBridge streamBridge) {
this.streamBridge = streamBridge;
}
public void publishEnrollmentCompletedEvent(EnrollmentCompletedEvent event) {
streamBridge.send("enrollmentCompleted-out-0", event);
}
}
Listener Example – Notification Service

java


package com.campusconnect.notification.listener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
@Component
public class EnrollmentCompletedListener {
@Bean
public Consumer<Message<EnrollmentCompletedEvent>> enrollmentCompleted() {
return message -> {
EnrollmentCompletedEvent event = message.getPayload();
System.out.println("Sending notification for: " + event.getStudentEmail());
};
}
}
Kafka Binding in application.yml
yaml


spring:
cloud:
stream:
bindings:
enrollmentCompleted-out-0:
destination: enrollment.completed.topic
enrollmentCompleted-in-0:
destination: enrollment.completed.topic
kafka:
binder:
brokers: kafka:9092
4. Containerization — Multi-Stage Dockerfile (Distroless optimized)
   dockerfile


# ---- Build Stage ----
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw clean package -DskipTests
# ---- Runtime Stage ----
FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/target/gateway-service-*.jar app.jar
USER nonroot:nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
5. Kubernetes YAML for API Gateway
   configmap.yaml
   yaml


apiVersion: v1
kind: ConfigMap
metadata:
name: gateway-config
data:
SPRING_PROFILES_ACTIVE: "prod"
SERVER_PORT: "8080"
gateway-deployment.yaml
yaml


apiVersion: apps/v1
kind: Deployment
metadata:
name: api-gateway
labels:
app: api-gateway
spec:
replicas: 3
selector:
matchLabels:
app: api-gateway
template:
metadata:
labels:
app: api-gateway
spec:
containers:
- name: api-gateway
image: campusconnect/api-gateway:1.0.0
imagePullPolicy: Always
ports:
- containerPort: 8080
envFrom:
- configMapRef:
name: gateway-config
readinessProbe:
httpGet:
path: /actuator/health
port: 8080
gateway-service.yaml
yaml


apiVersion: v1
kind: Service
metadata:
name: api-gateway
spec:
type: ClusterIP
selector:
app: api-gateway
ports:
- port: 80
targetPort: 8080
protocol: TCP
name: http
gateway-ingress.yaml
yaml


apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
name: campusconnect-ingress
annotations:
kubernetes.io/ingress.class: nginx
cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
tls:
- hosts:
- campusconnect.example.com
secretName: campusconnect-tls
rules:
- host: campusconnect.example.com
http:
paths:
- path: /
pathType: Prefix
backend:
service:
name: api-gateway
port:
number: 80
Key Outputs & Operational Benefits
Saga + Kafka ensures atomic but loosely coupled distributed transactions.
CQRS enables scalable reads under course/grade query loads.
Resilience4j isolates faults with retries and fallback.
Distroless images reduce attack surface and image size.
Kubernetes provides declarative scalability and self-healing.
OpenTelemetry (instrumentation not shown) enables unified tracing across services.
This blueprint provides a realistic, enterprise-level foundation ready for expansion into CI/CD pipelines, service mesh (e.g., Istio), and cloud observability suites.










