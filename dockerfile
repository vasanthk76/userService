# Use OpenJDK 22 as the base image
FROM amazoncorretto:17

LABEL maintainer="vasanthk11111"

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot application JAR file into the container
COPY target/demo-0.0.1-SNAPSHOT.jar /app/my-spring-app.jar

# Expose the application port (default Spring Boot port is 8080)
EXPOSE 4000

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "my-spring-app.jar"]
