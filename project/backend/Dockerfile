# Use official Eclipse Temurin JDK 17 base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy jar file into container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Fly uses 8080 internally)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
