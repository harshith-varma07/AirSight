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
- **📁 PDF/CSV Exports**: Download detailed air quality reports with embedded charts
- **📱 SMS Alerts**: Get notified when AQI exceeds your threshold via Twilio
- **👤 Personal Dashboard**: Manage alerts and download history
- **🔔 Multi-City Alerts**: Set different thresholds for multiple cities
- **📊 Advanced Analytics**: Dedicated analytics page with multiple chart types
- **🐍 Python-Powered Reports**: Enhanced PDF reports with matplotlib charts

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 2.7.14
- **Database:** MySQL 8 (H2 for testing)
- **Frontend:** HTML5, CSS3, JavaScript (ES6+), Chart.js
- **Analytics:** Python 3.8+, pandas, numpy, matplotlib, seaborn
- **PDF Generation:** ReportLab (Python), iText (Java)
- **Real-time Communication:** Spring WebSocket
- **Alerting:** Spring Scheduled Tasks, Twilio SMS integration
- **API:** RESTful endpoints with OpenAQ API integration

## 🚀 Quick Start

### Prerequisites
- Java 17+ (tested with Java 21)
- Maven 3.6+
- MySQL 8+ (or use H2 for testing)
- Python 3.8+ (for analytics features)

### Option 1: Quick Test with H2 Database

1. **Clone the repository**
   ```bash
   git clone https://github.com/harshith-varma07/AirSight.git
   cd AirSight
   ```

2. **Start the application**
   ```bash
   ./mvnw spring-boot:run
   # Windows: .\mvnw.cmd spring-boot:run
   ```

3. **Access the application**
   - Frontend: Open `frontend/index.html` in your browser
   - API Health Check: http://localhost:8080/api/health
   - H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:testdb`)

### Option 2: MySQL Setup

1. **Install MySQL and create database**
   ```sql
   CREATE DATABASE air_quality_monitoring;
   ```

2. **Run database setup**
   ```bash
   mysql -u root -p < database_setup.sql
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_monitoring
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Start the application**
   ```bash
   ./mvnw spring-boot:run
   ```

### Option 3: Docker Deployment

1. **Navigate to docker folder**
   ```bash
   cd docker
   ```

2. **Copy environment template**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start services**
   ```bash
   docker-compose up -d
   ```

## 📁 Project Structure

```
AirSight/
├── src/
│   ├── main/
│   │   ├── java/com/air/airquality/
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── services/            # Business logic (optimized)
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── model/               # JPA entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── util/                # Utility classes
│   │   │   ├── config/              # Configuration classes
│   │   │   └── exception/           # Global exception handling
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/              # Static web resources
│   └── test/                        # Unit and integration tests
├── frontend/                        # Frontend application
│   ├── index.html                   # Main HTML file
│   ├── about.html                   # About page
│   ├── analytics.html               # Analytics dashboard
│   ├── styles.css                   # Glassmorphism styling
│   ├── script.js                    # Main JavaScript logic
│   ├── analytics.js                 # Analytics functionality
│   └── charts.js                    # Chart management
├── docker/                          # Container configuration
│   ├── Dockerfile                   # Multi-stage build
│   ├── docker-compose.yml           # Service orchestration
│   ├── nginx.conf                   # Reverse proxy config
│   └── .env.example                 # Environment template
├── scripts/                         # Deployment automation
│   ├── deploy.sh                    # Linux/Mac deployment
│   └── deploy.bat                   # Windows deployment
├── python-analytics/                # Python analytics service
│   └── analytics_service.py         # Chart generation service
├── database_setup.sql               # Database schema
└── README.md                        # This file
```

## 🔧 Key Features & Configuration

### Authentication
- **Type**: Header-based authentication (`Authorization: Basic <credentials>`)
- **Registration**: Available via `/api/auth/register`
- **Login**: Available via `/api/auth/login`

### API Endpoints

#### Public Endpoints
- `GET /api/aqi/cities` - Get available cities
- `GET /api/aqi/current/{city}` - Get current AQI for city
- `GET /api/aqi/multiple` - Get AQI for multiple cities
- `POST /api/aqi/cities/add` - Add new city to monitoring
- `GET /api/health` - Application health check

#### Protected Endpoints (Premium)
- `GET /api/aqi/historical` - Historical AQI data
- `GET /api/export/pdf` - Generate PDF reports
- `GET /api/export/csv` - Export CSV data
- `GET /api/alerts/*` - Alert management
- `GET /api/user/*` - User profile management

### External Integrations
- **OpenAQ API**: Real-time air quality data
- **Twilio SMS**: Alert notifications (optional)
- **Fallback Data**: Built-in sample data when external APIs fail

### Performance Optimizations
- **Concurrent Data Structures**: ConcurrentHashMap for O(1) cache lookups
- **Parallel Processing**: Parallel streams for batch operations (40-60% improvement)
- **Algorithmic Improvements**: Binary search for AQI calculations, lookup tables
- **Memory Optimization**: Efficient fallback data structures
- **API Optimization**: Request batching and caching

## 📊 Analytics Features

### Setup Analytics
1. **Install Python Dependencies**
   ```bash
   # Windows
   setup-python-analytics.bat
   
   # Linux/Mac
   bash setup-python-analytics.sh
   ```

2. **Access Analytics Dashboard**
   - Login to your account
   - Navigate to Analytics via user menu
   - Select city and date range
   - Click "Load Analytics"

### Available Charts
- **AQI Trend Chart**: Shows air quality changes over time
- **Pollutants Bar Chart**: Average levels of different pollutants
- **AQI Categories Pie Chart**: Distribution of AQI categories
- **Pollution Distribution**: Statistical analysis of pollution levels

## 🐳 Production Deployment

### Pre-deployment Checklist
- [ ] Configure environment variables in `docker/.env`
- [ ] Set up database credentials
- [ ] Configure OpenAQ API key (optional but recommended)
- [ ] Set up Twilio for SMS alerts (optional)
- [ ] Configure SSL certificates for HTTPS
- [ ] Set up monitoring and alerting

### Deployment Commands
```bash
# Using deployment scripts
./scripts/deploy.sh    # Linux/Mac
scripts\deploy.bat     # Windows

# Manual deployment
docker-compose -f docker/docker-compose.yml up -d
```

### Post-deployment Verification
- [ ] Health check: `curl http://localhost:8080/api/health`
- [ ] Frontend loading: Visit application URL
- [ ] API endpoints: Test `/api/aqi/cities`
- [ ] User registration and login flow
- [ ] Premium features (if configured)

## 🧪 Testing

### Run Tests
```bash
mvn test                    # Unit tests
mvn integration-test        # Integration tests
mvn clean compile          # Build verification
```

### Manual Testing
1. **Frontend**: Open `frontend/index.html` in browser
2. **API**: Use `frontend/api-test.html` for endpoint testing
3. **Health**: Check `http://localhost:8080/api/health`

## 🔧 Troubleshooting

### Common Issues

1. **Port Conflicts**
   - Change ports in `docker/.env` or `application.properties`
   - Default backend port: 8080

2. **Database Connection**
   - Verify MySQL is running
   - Check credentials in configuration
   - For H2: Use JDBC URL `jdbc:h2:mem:testdb`

3. **API Rate Limits**
   - Add OpenAQ API key to configuration
   - Application includes fallback data

4. **Frontend-Backend Connection**
   - Ensure backend is running on port 8080
   - Check CORS configuration for your domain
   - Verify API base URL in `frontend/script.js`

### Debug Commands
```bash
# View application logs
docker-compose logs -f app

# Check database
mysql -u root -p -e "SHOW DATABASES;"

# Test API endpoints
curl http://localhost:8080/api/health
curl http://localhost:8080/api/aqi/cities
```

## 👥 Default Login Credentials

After starting the application, you can use these test accounts:

- **Admin**: username=`admin`, password=`admin123`
- **Test User**: username=`test`, password=`test123`

## 🔒 Security Features

- BCrypt password hashing
- Header-based authentication
- CORS configuration
- Rate limiting (via Nginx)
- Input validation
- SQL injection protection (JPA)

## 📈 Monitoring & Health Checks

- **Health Endpoint**: `/api/health` - Database, external API status
- **Actuator Endpoints**: Spring Boot Actuator for detailed metrics
- **Database Monitoring**: Connection pool status, query performance
- **External API Monitoring**: OpenAQ API availability and response times

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `mvn test`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:
1. Check this README for common solutions
2. Review application logs
3. Test API endpoints using the provided test page
4. Ensure all dependencies are properly installed

---

**AirSight** - Making air quality monitoring accessible to everyone. 🌍💨