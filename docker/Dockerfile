# Multi-stage build for optimized image size
FROM maven:3.8.6-openjdk-17-slim AS builder
WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Production stage
FROM openjdk:17-jre-slim
WORKDIR /app

# Create non-root user for security
RUN groupadd -r airsight && useradd -r -g airsight airsight

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR
COPY --from=builder /app/target/air-quality-monitoring-*.jar app.jar

# Copy frontend files
COPY frontend ./frontend

# Create logs directory
RUN mkdir -p logs && chown -R airsight:airsight /app

# Switch to non-root user
USER airsight

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# JVM optimization flags for container
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
