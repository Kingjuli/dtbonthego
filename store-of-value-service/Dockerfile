FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR file
COPY target/*.jar app.jar

# Set the user to run the application
USER spring:spring

# Set the entry point with fixed heap memory
ENTRYPOINT ["java", "-Xms512m", "-Xmx512m", "-jar", "app.jar"]

# Expose the application and management ports
EXPOSE 8082 9095