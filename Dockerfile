# First stage: build with Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy all project files
COPY . .

# Build the project (skip tests to speed up)
RUN mvn clean package -DskipTests

# Second stage: run with OpenJDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/target/Email-Writer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
