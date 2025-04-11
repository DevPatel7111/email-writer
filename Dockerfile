# Use an official JDK runtime as base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/Email-Writer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (same as in your application.properties)
EXPOSE 8080

# Set the entry point to run your jar
ENTRYPOINT ["java", "-jar", "app.jar"]
