# AirSight - Real-Time Air Quality Monitoring System

A comprehensive real-time air quality monitoring system built with Spring Boot 2.7.14, Java 21, MySQL, and a ## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 2.7.14
- **Database:** MySQL 8
- **Frontend:** HTML5, CSS3, JavaScript (ES6+), Chart.js
- **Analytics:** Python 3.8+, pandas, numpy, matplotlib, seaborn
- **PDF Generation:** ReportLab (Python), iText (Java)
- **Real-time Communication:** Spring WebSocket
- **Alerting:** Spring Scheduled Tasks, Twilio SMS integration
- **API:** RESTeb frontend.

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
- **📁 PDF/CSV Exports**: Download detailed air quality reports with embedded charts
- **📱 SMS Alerts**: Get notified when AQI exceeds your threshold via Twilio
- **👤 Personal Dashboard**: Manage alerts and download history
- **🔔 Multi-City Alerts**: Set different thresholds for multiple cities
- **📊 Advanced Analytics**: Dedicated analytics page with multiple chart types
- **🐍 Python-Powered Reports**: Enhanced PDF reports with matplotlib charts and comprehensive analytics

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
- **Analytics Dashboard**: Dedicated analytics page with interactive charts
- **Chart.js Integration**: Advanced data visualization with Chart.js

### Python Analytics Service
- **Advanced Analytics**: Statistical analysis with pandas and numpy
- **Chart Generation**: Professional charts using matplotlib and seaborn
- **Enhanced PDF Reports**: Comprehensive reports with embedded charts using ReportLab
- **Data Processing**: Time-series analysis and trend detection

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
- Python 3.8+ (for enhanced analytics and PDF reports)
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

### 3. Python Analytics Setup (Optional - for Enhanced Features)
```bash
# Install Python dependencies for advanced analytics
setup-python-analytics.bat    # On Windows
# OR
bash setup-python-analytics.sh  # On Linux/Mac

# Manual installation
pip install -r python-requirements.txt
```

### 4. Build & Run
```bash
# Compile and run
mvn clean compile
mvn spring-boot:run

# Access the application
# Backend API: http://localhost:8080/api
# Frontend: Open frontend/index.html in browser
# Analytics: Open frontend/analytics.html for advanced charts
```

## 📊 Analytics Features

### Analytics Dashboard (`frontend/analytics.html`)
- **AQI Trend Chart**: Time-series visualization of air quality changes
- **Pollutants Bar Chart**: Average levels of PM2.5, PM10, NO2, SO2, CO, O3
- **AQI Categories Pie Chart**: Distribution of air quality categories
- **Pollution Level Distribution**: Frequency of different pollution levels
- **Export Options**: Download individual charts or complete analytics reports
- **Time Period Selection**: Analyze any custom date range
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
GET  /api/export/analytics-pdf   # Enhanced PDF with charts (Python)
GET  /api/export/analytics-stats # Get comprehensive analytics statistics
GET  /api/export/chart/{type}    # Generate specific chart types
```

### Analytics Endpoints (Python-powered)
```http
GET  /api/export/analytics-pdf?city={city}&startDate={start}&endDate={end}
     # Enhanced PDF report with multiple charts and comprehensive analytics
     
GET  /api/export/analytics-stats?city={city}&startDate={start}&endDate={end}
     # Detailed statistics including trend analysis and pollutant averages
     
GET  /api/export/chart/{chartType}?city={city}&startDate={start}&endDate={end}
     # Individual chart generation (trend_chart, bar_chart, pie_chart, dist_chart)
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
- Python 3.8 or later (for enhanced analytics)
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
   - For analytics features, also access `frontend/analytics.html`.

4. **Python Analytics Setup (Optional):**
   ```bash
   # Run the setup script
   setup-python-analytics.bat    # Windows
   bash setup-python-analytics.sh  # Linux/Mac
   
   # Or manually install
   pip install -r python-requirements.txt
   ```

## 🎯 Key Features Walkthrough

### For All Users
1. **Real-time Monitoring**: Visit the homepage to see current air quality data
2. **City Search**: Use the search box to find and add new cities
3. **Global Overview**: View multiple cities simultaneously

### For Registered Users
1. **Sign Up/Login**: Create an account to unlock premium features
2. **Historical Analysis**: Access past air quality trends and patterns
3. **Advanced Analytics**: Visit `/analytics.html` for comprehensive data visualization
4. **Custom Reports**: Download detailed PDF reports with charts and insights
5. **SMS Alerts**: Set up notifications for air quality threshold breaches

### Analytics Dashboard Features
- **Interactive Charts**: Multiple visualization types for different insights
- **Time Period Selection**: Analyze any date range from your historical data  
- **Statistical Summary**: Key metrics including averages, peaks, and trends
- **Export Options**: Download individual charts or comprehensive reports
- **Real-time Updates**: Charts update automatically with new data
