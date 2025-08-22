# 🚀 AirSight Production Deployment Guide

This guide provides step-by-step instructions for deploying AirSight in production environments using Docker.

## 📋 Pre-Deployment Checklist

### System Requirements
- **OS**: Linux (Ubuntu 20.04+ recommended), Windows 10/11, or macOS 10.14+
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 20GB free space minimum
- **CPU**: 2 cores minimum, 4 cores recommended
- **Docker**: Docker Engine 20.10+ and Docker Compose 2.0+

### Network Requirements
- **Ports**: 80, 443 (Nginx), 8080 (Application), 3307 (MySQL), 6379 (Redis)
- **Outbound**: HTTPS access to openaq.org, api.twilio.com (if SMS enabled)
- **Domain**: Optional but recommended for production

## 🏗️ Architecture Overview

```
Internet
    │
    ▼
┌─────────────┐
│   Nginx     │  ← Reverse Proxy, SSL Termination, Load Balancing
│  (Port 80)  │
└─────────────┘
    │
    ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  AirSight   │────│   MySQL     │────│   Backup    │
│Application  │    │ (Port 3307) │    │   Service   │
│(Port 8080)  │    │             │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
    │
    ▼
┌─────────────┐
│    Redis    │  ← Caching Layer
│ (Port 6379) │
└─────────────┘
```

## 📁 Production File Structure

Create the following directory structure on your server:

```bash
/opt/airsight/
├── docker/
│   ├── docker-compose.yml
│   ├── docker-compose.prod.yml
│   ├── .env
│   ├── nginx.conf
│   ├── mysql-prod.cnf
│   ├── redis.conf
│   └── ssl/
│       ├── cert.pem
│       └── key.pem
├── backups/
├── logs/
└── monitoring/
```

## 🔧 Step-by-Step Deployment

### Step 1: Server Preparation

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Create application directory
sudo mkdir -p /opt/airsight
sudo chown $USER:$USER /opt/airsight
cd /opt/airsight

# Logout and login to refresh group membership
```

### Step 2: Application Setup

```bash
# Clone the repository
git clone https://github.com/harshith-varma07/AirSight.git .

# Navigate to docker directory
cd docker

# Copy and configure environment
cp .env.example .env
nano .env  # Edit configuration (see Configuration section below)
```

### Step 3: SSL Certificate Setup (Recommended for Production)

```bash
# Create SSL directory
mkdir -p ssl

# Option A: Let's Encrypt (Recommended)
sudo apt install certbot
sudo certbot certonly --standalone -d your-domain.com
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem ssl/cert.pem
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem ssl/key.pem

# Option B: Self-signed certificate (Development only)
openssl req -x509 -newkey rsa:4096 -keyout ssl/key.pem -out ssl/cert.pem -days 365 -nodes

# Set proper permissions
sudo chown $USER:$USER ssl/*
chmod 600 ssl/key.pem
chmod 644 ssl/cert.pem
```

### Step 4: Environment Configuration

Edit `.env` file with production values:

```bash
# Required Configuration
DB_ROOT_PASSWORD=your_very_secure_root_password_here_use_generator
DB_USER=airsight_prod_user
DB_PASSWORD=your_very_secure_db_password_here_use_generator

# Optional but Recommended
OPENAQ_API_KEY=your_openaq_api_key_from_openaq.org

# SMS Alerts (Optional)
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token  
TWILIO_PHONE_NUMBER=+1234567890

# Production Settings
ENVIRONMENT=production
LOG_LEVEL=INFO
NGINX_PORT=80
NGINX_SSL_PORT=443
TZ=UTC

# Admin Account
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_admin_password_here

# Performance Tuning
JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

### Step 5: Deploy Application

```bash
# Deploy with production configuration
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# OR use the deployment script
../scripts/deploy.sh production full

# Monitor deployment
docker-compose logs -f
```

### Step 6: Verify Deployment

```bash
# Check service status
docker-compose ps

# Health checks
curl -f http://localhost:8080/api/health
curl -f http://localhost:8080/actuator/health

# Test frontend
curl -I http://localhost

# Check logs for errors
docker-compose logs app | grep ERROR
docker-compose logs mysql | grep ERROR
docker-compose logs nginx | grep error
```

## 🔒 Security Hardening

### Firewall Configuration

```bash
# Enable UFW firewall
sudo ufw enable

# Allow essential ports
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS

# Block direct access to application services
sudo ufw deny 8080   # Block direct application access
sudo ufw deny 3307   # Block direct database access
sudo ufw deny 6379   # Block direct Redis access

# Check status
sudo ufw status verbose
```

### Container Security

```bash
# Run security scan
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  -v /tmp:/tmp --name clair-scanner \
  arminc/clair-scanner:latest --ip YOUR_LOCAL_IP airsight-app

# Update all containers regularly
docker-compose pull
docker-compose up -d
```

### Database Security

```bash
# Access MySQL container
docker-compose exec mysql mysql -u root -p

# Run security commands
FLUSH PRIVILEGES;
DELETE FROM mysql.user WHERE User='';
DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
DROP DATABASE IF EXISTS test;
DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';
FLUSH PRIVILEGES;
EXIT;
```

## 📊 Monitoring & Logging

### Log Management

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f nginx

# Monitor resource usage
docker-compose top
docker stats

# Set up log rotation
sudo nano /etc/logrotate.d/airsight
```

### Health Monitoring

```bash
# Create monitoring script
cat > /opt/airsight/monitor.sh << 'EOF'
#!/bin/bash
# AirSight Health Monitor

check_service() {
    if curl -sf http://localhost:8080/api/health > /dev/null; then
        echo "$(date): AirSight is healthy"
    else
        echo "$(date): AirSight health check failed!" 
        # Send alert here (email, Slack, etc.)
    fi
}

check_service >> /var/log/airsight-monitor.log
EOF

chmod +x /opt/airsight/monitor.sh

# Add to crontab (check every 5 minutes)
(crontab -l 2>/dev/null; echo "*/5 * * * * /opt/airsight/monitor.sh") | crontab -
```

## 💾 Backup Strategy

### Database Backup

```bash
# Create backup script
cat > /opt/airsight/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/airsight/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="airqualitydb"

# Create backup directory
mkdir -p $BACKUP_DIR

# Database backup
docker-compose exec -T mysql mysqldump -u root -p$DB_ROOT_PASSWORD $DB_NAME | gzip > $BACKUP_DIR/db_backup_$DATE.sql.gz

# Keep only last 30 days of backups
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +30 -delete

echo "$(date): Database backup completed: db_backup_$DATE.sql.gz"
EOF

chmod +x /opt/airsight/backup.sh

# Schedule daily backup at 2 AM
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/airsight/backup.sh") | crontab -
```

### Application Data Backup

```bash
# Backup volumes
docker run --rm -v airsight_mysql_data_prod:/data -v /opt/airsight/backups:/backup alpine tar czf /backup/mysql_data_$(date +%Y%m%d).tar.gz -C /data .

docker run --rm -v airsight_redis_data_prod:/data -v /opt/airsight/backups:/backup alpine tar czf /backup/redis_data_$(date +%Y%m%d).tar.gz -C /data .
```

## 🔄 Updates & Maintenance

### Application Updates

```bash
# Pull latest changes
cd /opt/airsight
git pull origin main

# Rebuild and deploy
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Verify update
curl -f http://localhost:8080/api/health
```

### System Maintenance

```bash
# Clean up unused Docker resources
docker system prune -a

# Update system packages
sudo apt update && sudo apt upgrade -y

# Restart services if needed
docker-compose restart
```

## 🚨 Troubleshooting

### Common Issues

**Service won't start:**
```bash
# Check logs
docker-compose logs service_name

# Check resource usage
df -h  # Disk space
free -h  # Memory usage
docker system df  # Docker disk usage
```

**Database connection issues:**
```bash
# Test database connectivity
docker-compose exec app nc -z mysql 3306

# Check MySQL logs
docker-compose logs mysql | tail -50

# Reset database (CAUTION: This will delete all data)
docker-compose down -v
docker-compose up -d mysql
```

**Performance issues:**
```bash
# Monitor resource usage
docker stats

# Check slow queries (MySQL)
docker-compose exec mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# Check Redis memory usage
docker-compose exec redis redis-cli INFO memory
```

### Performance Tuning

**Increase JVM memory:**
```bash
# In .env file
JAVA_OPTS=-Xmx4g -Xms2g -XX:+UseG1GC -XX:+UseContainerSupport
```

**Optimize MySQL:**
```bash
# Edit mysql-prod.cnf
innodb_buffer_pool_size = 1G  # Increase for more RAM
max_connections = 300         # Increase for high traffic
```

**Scale with Docker Swarm:**
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml airsight

# Scale application
docker service scale airsight_app=3
```

## 📞 Support & Maintenance

### Health Check URLs
- Application: `http://your-domain/api/health`
- Database: MySQL port 3307 (internal only)
- Cache: Redis port 6379 (internal only)

### Log Locations
- Application: `/opt/airsight/logs/`
- Nginx: Docker volume `nginx_logs`
- MySQL: Docker volume `mysql_logs`

### Configuration Files
- Main config: `/opt/airsight/docker/.env`
- Nginx: `/opt/airsight/docker/nginx.conf`
- MySQL: `/opt/airsight/docker/mysql-prod.cnf`
- Redis: `/opt/airsight/docker/redis.conf`

---

**🎉 Congratulations!** Your AirSight production deployment is now complete and ready to monitor air quality data 24/7!
