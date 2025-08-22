# Multi-stage build for optimized image size
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Production stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S airsight && \
    adduser -S airsight -u 1001 -G airsight

# Install curl and ca-certificates for health checks and HTTPS
RUN apk add --no-cache curl ca-certificates tzdata

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
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV TZ=UTC

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
