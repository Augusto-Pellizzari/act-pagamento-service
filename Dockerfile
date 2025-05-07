FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17.0-slim-buster
WORKDIR /app

COPY --from=builder /build/target/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
