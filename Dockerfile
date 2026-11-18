# Small Java 17 runtime image
FROM eclipse-temurin:17-jre

# Folder inside the container
WORKDIR /app

# Copy the jar that Maven built
COPY target/atu-project-board.jar app.jar

# The app listens on port 8080
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "app.jar"]