FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY ../common-library ./common-library
COPY . ./notification-service
RUN ./mvnw clean package -DskipTests -pl notification-service -am

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/notification-service/target/notification-service-*.jar app.jar
USER nonroot:nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
