# AirSight - Real-Time Air Quality Monitoring System

A comprehensive real-time air quality monitoring system built with Spring Boot 2.7.14, Java 21, MySQL, and a modern web frontend.

## 🌟 Features

### Public Features
- **Real-time AQI Display**: Current air quality index for cities worldwide
- **Global City Monitoring**: Track multiple cities simultaneously
- **Interactive Search**: Find and add new cities to monitoring
- **Responsive Design**: Modern glass-morphism UI with animations

### Premium Features (Registered Users)
- **📊 Historical Data Visualization**: Interactive charts showing AQI trends over time
- **📅 Custom Date Range Analysis**: Select any time period for detailed analysis
- **📈 Statistical Insights**: Average, max, min AQI values with data point counts
- **📁 PDF/CSV Exports**: Download detailed air quality reports
- **📱 SMS Alerts**: Get notified when AQI exceeds your threshold via Twilio
- **👤 Personal Dashboard**: Manage alerts and download history
- **🔔 Multi-City Alerts**: Set different thresholds for multiple cities

## 🏗️ Architecture

### Backend (Spring Boot 2.7.14 + Java 21)
- **Controllers**: REST API endpoints (`/api/aqi/*`, `/api/auth/*`, `/api/alerts/*`)
- **Services**: Business logic with OpenAQ API integration and fallback data
- **Scheduled Tasks**: Automatic data fetching every 5 minutes
- **Authentication**: Header-based auth with BCrypt password encoding
- **Database**: MySQL with JPA/Hibernate

### Frontend (Vanilla HTML/CSS/JS)
- **Separated Files**: Clean separation of HTML, CSS, and JavaScript
- **Dynamic UI**: Real-time updates with API integration
- **Responsive Design**: Works on desktop and mobile devices
- **Modern Animations**: Glass-morphism effects and smooth transitions

### Database Schema
```sql
- users: User accounts with authentication and preferences
- aqi_data: Time-series air quality measurements
- user_alerts: Alert history and settings
```

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js (optional, for development server)

### 1. Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE air_quality_monitoring;

# Run setup script
mysql -u root -p air_quality_monitoring < database_setup.sql
```

### 2. Configuration
Update `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.password=your_mysql_password

# OpenAQ API (optional)
openaq.api.key=your_openaq_api_key

# Twilio SMS (optional - leave empty to disable)
twilio.account.sid=your_twilio_sid
twilio.auth.token=your_twilio_token
twilio.phone.number=your_twilio_number
```

### 3. Build & Run
```bash
# Compile and run
mvn clean compile
mvn spring-boot:run

# Access the application
# Backend API: http://localhost:8080/api
# Frontend: Open frontend/index.html in browser
```

## � API Endpoints

### Public Endpoints
```http
GET  /api/aqi/current/{city}     # Get current AQI for a city
GET  /api/aqi/cities             # List all monitored cities
GET  /api/aqi/search?query=      # Search cities (with auto-add)
GET  /api/aqi/multiple?cities=   # Get multiple cities data
POST /api/aqi/cities/add         # Add new city to monitoring
```

### Premium Endpoints (Authentication Required)
```http
GET  /api/aqi/historical/{city}  # Get historical data with date range
     ?startDate=2025-08-01T00:00:00&endDate=2025-08-20T23:59:59
POST /api/auth/register          # User registration
POST /api/auth/login             # User login
GET  /api/users/profile          # Get user profile
PUT  /api/users/profile          # Update user profile
GET  /api/alerts                 # Get user's alerts
POST /api/alerts                 # Create new alert
GET  /api/export/pdf/{city}      # Export historical data as PDF
GET  /api/export/csv/{city}      # Export historical data as CSV
```

### Authentication
- **Headers Required**: `Authorization: Basic <base64(username:password)>`
- **User ID Header**: `X-User-Id: <user_id>` (for historical data access)

## �🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 2.7.14
- **Database:** MySQL 8
- **Frontend:** HTML5, CSS3, JavaScript (ES6+)
- **Real-time Communication:** Spring WebSocket
- **Alerting:** Spring Scheduled Tasks, integration with an email/SMS service (e.g., Twilio, SendGrid)
- **API:** REST

## 🚀 Getting Started

### Prerequisites

- Java JDK 17 or later
- Maven 3.8 or later
- MySQL 8 or later
- An IDE like IntelliJ IDEA or VS Code with Java extensions

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/AirSight.git
   cd AirSight
   ```
2. **Backend Setup:**
   - Navigate to the `backend` directory.
   - Update the `src/main/resources/application.properties` file with your MySQL database credentials.
   - Run the application: `mvn spring-boot:run`

3. **Frontend Setup:**
   - Open the `frontend/index.html` file in your web browser.
