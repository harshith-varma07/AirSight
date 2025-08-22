# Quick Setup Guide for AirSight Backend

## Issue: Backend Connection Failed

The frontend is working perfectly, but the backend needs to be properly configured and started.

## Solutions (Choose One)

### Option 1: Use H2 In-Memory Database (Easiest for Testing)

1. **Update `src/main/resources/application.properties`:**
```properties
# Replace existing database config with:
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

# Enable H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Server Configuration
server.port=8080

# External API Configuration (Optional)
openaq.api.url=https://api.openaq.org/v2/latest
openaq.api.key=
openaq.api.timeout=10000

# Disable SMS for testing
twilio.account.sid=
twilio.auth.token=
twilio.phone.number=
```

2. **Update `pom.xml` - Change H2 scope from `test` to `runtime`:**
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

3. **Start Backend:**
```bash
./mvnw.cmd spring-boot:run    # Windows
# or
./mvnw spring-boot:run        # Mac/Linux
```

### Option 2: Use MySQL Database (Production Setup)

1. **Install MySQL:**
   - Download and install MySQL Server
   - Create database: `CREATE DATABASE air_quality_monitoring;`

2. **Update `src/main/resources/application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_monitoring
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

3. **Run database setup:**
```bash
mysql -u root -p < database_setup.sql
```

4. **Start Backend:**
```bash
./mvnw.cmd spring-boot:run
```

### Option 3: Use Docker (Recommended for Development)

1. **Navigate to docker folder:**
```bash
cd docker
```

2. **Start services:**
```bash
docker-compose up -d
```

3. **Build and run application:**
```bash
cd ..
./mvnw.cmd spring-boot:run
```

## Verification Steps

1. **Backend Started:** Check for "Started AirQualityMonitoringApplication" in console
2. **Health Check:** Visit `http://localhost:8080/api/health`
3. **H2 Console (if using H2):** Visit `http://localhost:8080/h2-console`
4. **API Test:** Refresh `api-test.html` and click test buttons

## Current Status

✅ **Frontend:** All working perfectly
- Light theme applied
- Navigation updated (Home, About)
- About page created with comprehensive content
- All API endpoints configured correctly

❌ **Backend:** Not running
- Database connection issue (MySQL not running)
- Spring Boot application not started

## Quick Fix Command

Try this command in the project directory:

```bash
# Windows
.\mvnw.cmd clean spring-boot:run -Dspring.profiles.active=test

# Mac/Linux  
./mvnw clean spring-boot:run -Dspring.profiles.active=test
```

This will use the test profile with H2 database that should work without external dependencies.
