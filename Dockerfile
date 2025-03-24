FROM maven:3-openjdk-17 AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk-alpine

COPY --from=build /app/target/orgsync-0.0.1-SNAPSHOT.jar /app/app.jar


WORKDIR /app
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
