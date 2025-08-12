# Use OpenJDK 17 with Alpine for smaller image size
FROM openjdk:17-jdk-alpine

# Install fonts for better text rendering
RUN apk add --no-cache fontconfig ttf-dejavu

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/codesnapper-all.jar app.jar

# Expose the port the app runs on
EXPOSE 8081

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
