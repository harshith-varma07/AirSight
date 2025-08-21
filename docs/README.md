# AirSight - Optimized Air Quality Monitoring System

## 🌟 Project Overview

AirSight is a real-time air quality monitoring system built with Spring Boot 2.7.14, Java 17, and modern web technologies. It provides public AQI access and premium features for registered users including historical data analysis, PDF exports, and SMS alerts.

## 🏗️ Architecture Overview

### Backend Stack
- **Framework**: Spring Boot 2.7.14 with Java 17
- **Database**: MySQL 8.0 with H2 for testing
- **External APIs**: OpenAQ API for real-time data
- **Security**: Header-based authentication
- **Scheduling**: Spring `@Scheduled` for 5-minute updates
- **SMS Integration**: Twilio (optional)

### Frontend Stack
- **Technology**: Vanilla HTML5, CSS3, JavaScript ES6+
- **UI Framework**: Custom glassmorphism design
- **Charts**: Chart.js for data visualization
- **API Integration**: Fetch API with error handling
- **State Management**: Browser sessionStorage

### Performance Optimizations
- **Concurrent Data Structures**: ConcurrentHashMap for O(1) cache lookups
- **Parallel Processing**: Parallel streams for batch operations
- **Algorithmic Improvements**: Binary search for AQI calculations, lookup tables
- **Memory Optimization**: Efficient fallback data structures
- **API Optimization**: Request batching and caching

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ (or Docker)
- Docker & Docker Compose (for containerized deployment)

### Local Development
```bash
# Clone the repository
git clone <repository-url>
cd AirSight

# Setup database
mysql -u root -p < database_setup.sql

# Configure application
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties with your database credentials

# Run the application
mvn spring-boot:run

# Open frontend
# Navigate to http://localhost:8080 in your browser
```

### Docker Deployment
```bash
# Setup environment
cp docker/.env.example docker/.env
# Edit docker/.env with your configuration

# Deploy using script (Windows)
scripts\deploy.bat

# Deploy using script (Linux/Mac)
chmod +x scripts/deploy.sh
./scripts/deploy.sh

# Or manually with Docker Compose
docker-compose -f docker/docker-compose.yml up -d
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
│   │   │   ├── util/                # Utility classes (optimized AQI calculator)
│   │   │   ├── config/              # Configuration classes
│   │   │   └── exception/           # Global exception handling
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/              # Static web resources
│   └── test/                        # Unit and integration tests
├── frontend/                        # Frontend application
│   ├── index.html                   # Main HTML file
│   ├── styles.css                   # Glassmorphism styling
│   ├── script.js                    # Main JavaScript logic
│   └── charts.js                    # Chart management
├── docker/                          # Container configuration
│   ├── Dockerfile                   # Multi-stage build
│   ├── docker-compose.yml           # Service orchestration
│   ├── nginx.conf                   # Reverse proxy config
│   └── .env.example                 # Environment template
├── scripts/                         # Deployment automation
│   ├── deploy.sh                    # Linux/Mac deployment
│   └── deploy.bat                   # Windows deployment
├── database_setup.sql               # Database schema
└── README.md                        # This file
```

## 🔧 Key Features

### Public Features
- ✅ Real-time AQI data for multiple cities
- ✅ Interactive city search with auto-add functionality
- ✅ Responsive glassmorphism UI design
- ✅ Live parameter monitoring (PM2.5, PM10, NO2, O3, SO2, CO)
- ✅ AQI category visualization with color coding

### Premium Features (Registered Users)
- 🔐 User registration and authentication
- 📊 Historical data analysis with interactive charts
- 📄 PDF/CSV report generation
- 📱 SMS alert system via Twilio integration
- ⚙️ Customizable alert thresholds
- 📈 Statistical analytics (min, max, average AQI)

### Performance Features
- ⚡ Optimized AQI calculations using lookup tables
- 🔄 Concurrent data processing with parallel streams
- 💾 Smart caching with ConcurrentHashMap
- 🌐 Efficient API batching and fallback mechanisms
- 📱 Real-time updates every 5 minutes
- 🏥 Health check endpoints for monitoring

## 🛠️ API Endpoints

### Public Endpoints
- `GET /api/aqi/cities` - Get monitored cities
- `GET /api/aqi/current/{city}` - Get current AQI for city
- `GET /api/aqi/multiple?cities=...` - Batch AQI data
- `GET /api/aqi/search?query=...` - Search cities
- `POST /api/aqi/cities/add?city=...` - Add city to monitoring
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication

### Protected Endpoints (Premium)
- `GET /api/aqi/historical/{city}` - Historical data (requires auth)
- `GET /api/export/pdf` - Generate PDF report
- `GET /api/export/csv` - Generate CSV report
- `POST /api/alerts/create` - Create alert
- `GET /api/users/alerts` - Get user alerts
- `DELETE /api/alerts/{id}` - Delete alert

### System Endpoints
- `GET /api/health` - Health check
- `GET /api/ready` - Readiness probe

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/airqualitydb
spring.datasource.username=your_username
spring.datasource.password=your_password

# OpenAQ API (optional key for higher rate limits)
openaq.api.key=your_openaq_api_key

# Twilio SMS (optional)
twilio.account.sid=your_account_sid
twilio.auth.token=your_auth_token
twilio.phone.number=+1234567890

# Performance Tuning
alert.batch.size=50
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
```

### Docker Environment Variables
```bash
# Database
DB_ROOT_PASSWORD=secure_root_password
DB_USER=airsight_user
DB_PASSWORD=secure_password

# Application
OPENAQ_API_KEY=your_api_key
TWILIO_ACCOUNT_SID=your_sid
TWILIO_AUTH_TOKEN=your_token
TWILIO_PHONE_NUMBER=+1234567890

# Performance
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
```

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AqiControllerTest

# Run with coverage
mvn test jacoco:report
```

### Test Configuration
- **Security**: Custom `TestSecurityConfig` for test authentication
- **Database**: H2 in-memory database for tests
- **API Mocking**: Mock external API calls for reliable testing

## 📊 Performance Optimizations

### Data Structure Optimizations
- **ConcurrentHashMap**: O(1) cache lookups for frequently accessed data
- **Parallel Streams**: Concurrent processing for batch operations
- **Lookup Tables**: Pre-computed AQI breakpoints for faster calculations
- **Binary Search**: Efficient algorithm for AQI range determination

### Algorithm Improvements
- **AQI Calculator**: Replaced multiple if-else chains with lookup tables
- **City Normalization**: Optimized string processing for city matching
- **Batch Processing**: Group operations for database efficiency
- **Memory Management**: Efficient fallback data structures

### Database Optimizations
- **Query Optimization**: Replaced native queries with optimized JPA methods
- **Connection Pooling**: HikariCP configuration for optimal connections
- **Batch Operations**: Hibernate batch processing for bulk operations
- **Indexes**: Strategic indexing on frequently queried columns

## 🐳 Docker Deployment

### Production Deployment
```bash
# Start all services
docker-compose -f docker/docker-compose.yml up -d

# Scale application (if needed)
docker-compose -f docker/docker-compose.yml up -d --scale app=3

# View logs
docker-compose -f docker/docker-compose.yml logs -f

# Stop services
docker-compose -f docker/docker-compose.yml down
```

### Service Architecture
- **MySQL 8.0**: Primary database with persistent volume
- **Redis**: Caching layer (optional)
- **Nginx**: Reverse proxy with rate limiting
- **AirSight App**: Main application container
- **Health Checks**: Comprehensive service monitoring

## 🔍 Monitoring & Health Checks

### Health Check Endpoints
- **Health**: `/api/health` - Overall system health
- **Readiness**: `/api/ready` - Service readiness for traffic

### Monitoring Features
- Database connectivity checks
- External API health monitoring
- Memory and performance metrics
- Container health checks with auto-restart

## 🚀 Cloud Deployment Ready

### Features for Cloud Deployment
- ✅ Multi-stage Docker builds for smaller images
- ✅ Non-root user for security
- ✅ Health checks and readiness probes
- ✅ Environment-based configuration
- ✅ Logging to stdout/stderr
- ✅ Graceful shutdown handling
- ✅ Resource optimization flags
- ✅ SSL/TLS ready with Nginx
- ✅ Rate limiting and security headers

### Kubernetes Ready
The application includes all necessary configurations for Kubernetes deployment:
- Health and readiness probes
- Environment variable configuration
- Service discovery compatible
- Stateless application design
- Persistent volume support for database

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues
1. **Port 8080 already in use**: Change `APP_PORT` in docker/.env
2. **Database connection failed**: Check MySQL credentials and ensure service is running
3. **OpenAQ API rate limits**: Add your API key in configuration
4. **SMS not working**: Verify Twilio credentials or disable SMS alerts

### Support
- Check the [Issues](issues) page for common problems
- Review application logs: `docker-compose logs -f`
- Verify health endpoints: `curl http://localhost:8080/api/health`

---

🌍 **AirSight** - Making air quality data accessible to everyone, everywhere.
