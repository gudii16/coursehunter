FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY ../pom.xml ./pom.xml
COPY ../common-library ./common-library
COPY . ./course-service
RUN ./mvnw clean package -DskipTests -pl course-service -am

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
COPY --from=builder /app/course-service/target/course-service-*.jar app.jar
USER nonroot:nonroot
ENTRYPOINT ["java", "-jar", "app.jar"]
