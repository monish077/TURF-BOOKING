# Use Java 21 for better SSL/TLS support with MongoDB Atlas
FROM eclipse-temurin:21-jdk-alpine

# Set working directory in container
WORKDIR /app

# Install CA certificates for SSL connections
RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

# Copy Maven wrapper and pom.xml first (better caching)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable and download dependencies
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the application - USING WILDCARD (more reliable)
CMD java -jar target/*.jar