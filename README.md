# AirSight - Real-Time Air Quality Monitoring System

A comprehensive real-time air quality monitoring system built with Spring Boot 2.7.14, Java 17, MySQL, and a modern web frontend. Deploy anywhere with Docker in under 5 minutes! 🚀

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

- **Backend:** Java 17, Spring Boot 2.7.14
- **Database:** MySQL 8 (H2 for development/testing)
- **Cache:** Redis 7 (optional but recommended)
- **Frontend:** HTML5, CSS3, JavaScript (ES6+), Chart.js
- **Analytics:** Python 3.8+, pandas, numpy, matplotlib, seaborn
- **PDF Generation:** ReportLab (Python), iText (Java)
- **Container:** Docker & Docker Compose
- **Reverse Proxy:** Nginx (production)
- **API:** RESTful endpoints with OpenAQ API integration

## 🚀 Quick Start Options

### Option 1: 🐳 Docker Deployment (Recommended)

**Prerequisites:**
- Docker Desktop (Windows/Mac) or Docker Engine + Docker Compose (Linux)
- 4GB RAM minimum, 8GB recommended
- 5GB free disk space

**1. Clone and Deploy:**
```bash
git clone https://github.com/harshith-varma07/AirSight.git
cd AirSight

# Quick deployment with scripts
./scripts/deploy.sh              # Linux/Mac
scripts\deploy.bat              # Windows

# Or manual deployment
cd docker
cp .env.example .env
# Edit .env with your configuration
docker-compose up -d
```

**2. Access Application:**
- 🌐 **Frontend**: http://localhost:8080
- 🏥 **Health Check**: http://localhost:8080/api/health
- 🗄️ **Database**: localhost:3307 (MySQL)

**That's it! 🎉** The application is now running with all services.

### Option 2: 🔧 Development Setup (Local)

**Prerequisites:**
- Java 17+ (Java 17 or higher recommended)
- Maven 3.6+
- MySQL 8+ or use H2 for testing

**1. Quick Test with H2:**
```bash
git clone https://github.com/harshith-varma07/AirSight.git
cd AirSight
./mvnw spring-boot:run
```

**2. Production with MySQL:**
```bash
# Setup database
mysql -u root -p < database_setup.sql

# Update application.properties
# spring.datasource.url=jdbc:mysql://localhost:3306/air_quality_monitoring
# spring.datasource.username=root
# spring.datasource.password=your_password

# Start application
./mvnw spring-boot:run
```

**3. Access Application:**
- Frontend: Open `frontend/index.html` in browser
- API: http://localhost:8080/api/health

## 🐳 Production Docker Deployment

### Architecture Overview
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    Nginx    │────│  AirSight   │────│   MySQL     │
│  (Port 80)  │    │ Application │    │ (Port 3307) │
│             │    │ (Port 8080) │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                           │
                   ┌─────────────┐
                   │    Redis    │
                   │ (Port 6379) │
                   └─────────────┘
```

### Environment Configuration

**1. Create Environment File:**
```bash
cd docker
cp .env.example .env
```

**2. Configure Key Settings in `.env`:**
```env
# Database Security
DB_ROOT_PASSWORD=your_secure_root_password
DB_USER=airsight_user
DB_PASSWORD=your_secure_db_password

# External APIs
OPENAQ_API_KEY=your_openaq_api_key     # Get from https://openaq.org/

# SMS Alerts (Optional)
TWILIO_ACCOUNT_SID=your_twilio_sid
TWILIO_AUTH_TOKEN=your_twilio_token
TWILIO_PHONE_NUMBER=+1234567890

# Performance
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
```

### Deployment Profiles

**Basic Profile** (Development):
```bash
# Starts: MySQL + Redis + Application
docker-compose up -d mysql redis app
```

**Full Profile** (Production):
```bash
# Starts: All services including Nginx reverse proxy
docker-compose --profile production up -d
```

### Advanced Deployment

**1. Production Deployment:**
```bash
# Using deployment script
./scripts/deploy.sh production full

# Manual with custom profile
docker-compose --profile production up -d
```

**2. SSL Configuration:**
```bash
# Add SSL certificates to docker/ssl/
mkdir -p docker/ssl
# Copy cert.pem and key.pem to docker/ssl/
# Uncomment HTTPS server block in nginx.conf
```

**3. Monitoring & Logs:**
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f nginx

# Monitor resource usage
docker-compose top
```

## 📁 Project Structure

```
AirSight/
├── 🚀 Quick Start & Deployment
│   ├── scripts/
│   │   ├── deploy.sh              # Linux/Mac deployment
│   │   ├── deploy.bat             # Windows deployment  
│   │   └── generate-env.sh        # Auto-generate configs
│   ├── docker/
│   │   ├── Dockerfile             # Multi-stage production build
│   │   ├── docker-compose.yml     # Service orchestration
│   │   ├── nginx.conf             # Reverse proxy config
│   │   ├── redis.conf             # Redis configuration
│   │   ├── .env.example           # Environment template
│   │   ├── .env                   # Generated standard config
│   │   └── .env.gcp               # Generated GCP config
│   ├── terraform/                 # Infrastructure as Code
│   │   ├── main.tf               # Terraform configuration
│   │   ├── terraform.tfvars.example
│   │   └── README.md             # Terraform deployment guide
│   ├── cloudbuild.yaml            # Google Cloud Build config
│   ├── app.yaml                   # App Engine config
│   ├── k8s-manifest.yaml          # Kubernetes manifests
│   ├── DEPLOYMENT-GUIDE.md        # Google Cloud deployment guide
│   └── database_setup.sql         # Database schema & sample data
│
├── 🏗️ Application Source
│   ├── src/main/java/com/air/airquality/
│   │   ├── controller/            # REST API endpoints
│   │   ├── services/              # Business logic (optimized)
│   │   ├── repository/            # Data access layer
│   │   ├── model/                 # JPA entities
│   │   ├── dto/                   # Data transfer objects
│   │   ├── config/                # Configuration classes
│   │   └── exception/             # Global exception handling
│   └── src/main/resources/
│       ├── application*.properties # Environment configs
│       └── static/                # Static resources
│
├── 🎨 Frontend Application
│   ├── index.html                 # Main application
│   ├── analytics.html             # Analytics dashboard  
│   ├── styles.css                 # Glassmorphism styling
│   ├── script.js                  # Core JavaScript
│   └── analytics.js               # Analytics functionality
│
└── 🐍 Python Analytics
    ├── analytics_service.py       # Chart generation service
    ├── python-requirements.txt    # Python dependencies
    └── setup-python-analytics.sh  # Setup script
```

## 🔧 Configuration Guide

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | `airsightpass` | MySQL database password |
| `OPENAQ_API_KEY` | *(empty)* | OpenAQ API key (recommended) |
| `TWILIO_ACCOUNT_SID` | *(empty)* | Twilio SMS service ID |
| `APP_PORT` | `8080` | Application port |
| `LOG_LEVEL` | `INFO` | Application log level |
| `JAVA_OPTS` | `-Xmx512m...` | JVM optimization flags |

### API Endpoints

#### Public Endpoints (No Authentication)
```http
GET  /api/health                  # Application health status
GET  /api/aqi/cities             # Available cities list
GET  /api/aqi/current/{city}     # Current AQI for city
GET  /api/aqi/multiple           # AQI for multiple cities
POST /api/aqi/cities/add         # Add new city to monitoring
POST /api/auth/register          # User registration
POST /api/auth/login             # User login
```

#### Premium Endpoints (Authentication Required)
```http
GET  /api/aqi/historical         # Historical AQI data
GET  /api/export/pdf             # Generate PDF reports
GET  /api/export/csv             # Export CSV data
GET  /api/alerts/*               # Alert management
GET  /api/user/*                 # User profile management
```

### Authentication
- **Type**: Header-based authentication
- **Header**: `Authorization: Basic <base64(username:password)>`
- **Registration**: Available via `/api/auth/register`
- **Frontend Storage**: `sessionStorage` for user sessions

## 📊 Analytics & Reporting

### Setup Analytics Features
```bash
# Install Python dependencies
# Windows
setup-python-analytics.bat

# Linux/Mac  
bash setup-python-analytics.sh

# Verify setup
python python-analytics/analytics_service.py --test
```

### Available Analytics
- **AQI Trend Charts**: Time-series visualization
- **Pollutant Analysis**: Bar charts for different pollutants  
- **Category Distribution**: Pie charts for AQI categories
- **Statistical Reports**: PDF exports with embedded charts

## 🧪 Testing & Development

### Running Tests
```bash
# Unit tests
./mvnw test

# Integration tests  
./mvnw integration-test

# Test with H2 database
./mvnw spring-boot:run -Dspring.profiles.active=test

# API endpoint testing
# Open frontend/api-test.html in browser
```

### Development Mode
```bash
# Hot reload with dev tools
./mvnw spring-boot:run -Dspring.profiles.active=dev

# With H2 console access
# H2 Console: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
```

## 🛡️ Security Features

- ✅ **BCrypt Password Hashing**: Secure password storage
- ✅ **Header-based Authentication**: Simple and effective auth
- ✅ **CORS Configuration**: Cross-origin request management  
- ✅ **Rate Limiting**: API protection via Nginx
- ✅ **Input Validation**: Request validation with Spring
- ✅ **SQL Injection Protection**: JPA prevents SQL injection
- ✅ **Container Security**: Non-root user in Docker containers

## 📈 Performance Optimizations

- **Concurrent Data Structures**: O(1) cache lookups
- **Parallel Processing**: 40-60% improvement in batch operations
- **Database Optimization**: Connection pooling, batch inserts
- **Caching Strategy**: Redis for frequently accessed data
- **JVM Tuning**: Container-aware G1 garbage collector
- **API Optimization**: Request batching and intelligent fallbacks

## 🔧 Troubleshooting

### Common Issues

**🐳 Docker Issues:**
```bash
# Port already in use
sudo lsof -i :8080                # Check what's using port 8080
docker-compose down               # Stop all services

# Permission denied (Linux)
sudo usermod -aG docker $USER     # Add user to docker group
newgrp docker                     # Refresh group membership

# Out of disk space
docker system prune -a            # Clean up unused images
```

**🗄️ Database Issues:**
```bash
# Connection refused
docker-compose logs mysql         # Check MySQL logs
docker-compose restart mysql     # Restart MySQL

# Reset database
docker-compose down -v            # Remove volumes
docker-compose up -d              # Recreate with fresh data
```

**🌐 API Issues:**
```bash
# Health check failing
curl -v http://localhost:8080/api/health

# CORS issues  
# Check CORS_ALLOWED_ORIGINS in .env file

# Rate limiting
# Check nginx logs: docker-compose logs nginx
```

### Debug Commands
```bash
# Application logs
docker-compose logs -f app

# Database access
docker-compose exec mysql mysql -u airsight_user -p airqualitydb

# Redis access
docker-compose exec redis redis-cli

# Container resource usage
docker-compose top

# Network inspection
docker network ls
docker network inspect airsight_airsight-network
```

## 🚀 Deployment Checklist

### Google Cloud Platform
- [ ] Set up GCP project and enable APIs
- [ ] Update `terraform/terraform.tfvars` with your project ID
- [ ] Run `./scripts/generate-env.sh` to create configs
- [ ] Deploy with `terraform apply` or `gcloud builds submit`
- [ ] Update Secret Manager with API keys and credentials

### Docker Deployment  
- [ ] Run `./scripts/generate-env.sh` to generate configuration
- [ ] Update `docker/.env` with secure passwords and API keys
- [ ] Configure domain name and SSL certificates (production)
- [ ] Run `docker-compose up -d` to start services

### Post-deployment
- [ ] Verify health check: `curl https://your-domain/api/health`
- [ ] Test user registration and premium features
- [ ] Configure monitoring and alerting
- [ ] Set up automated backups

## 👥 Default Test Accounts

After starting the application, use these credentials:
- **Admin**: `admin` / `admin123`
- **Test User**: `test` / `test123`

## 📚 Additional Resources

- **OpenAQ API**: https://openaq.org/
- **Twilio SMS**: https://www.twilio.com/
- **Docker Documentation**: https://docs.docker.com/
- **Spring Boot Docs**: https://spring.io/projects/spring-boot

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Run tests: `./mvnw test`
4. Commit changes: `git commit -m 'Add amazing feature'`
5. Push to branch: `git push origin feature/amazing-feature`
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

**Need Help?**
1. 📖 Check this README for solutions
2. 🐛 Review application logs: `docker-compose logs`
3. 🧪 Test API endpoints using `frontend/api-test.html`
4. 💬 Open an issue on GitHub

---

**🌍 AirSight - Making air quality monitoring accessible to everyone!** 

Built with ❤️ by the AirSight team
