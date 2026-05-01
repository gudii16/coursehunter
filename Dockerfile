# Build stage
FROM eclipse-temurin:21-jdk AS builder
ARG SERVICE_NAME
WORKDIR /app
COPY ${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar

# Runtime stage (distroless — no shell, minimal attack surface)
FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
USER nonroot:nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
