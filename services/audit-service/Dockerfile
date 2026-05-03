FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY ../common-library ./common-library
COPY . ./audit-service
RUN ./mvnw clean package -DskipTests -pl audit-service -am

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/audit-service/target/audit-service-*.jar app.jar
USER nonroot:nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
