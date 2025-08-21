# Production deployment checklist for AirSight

## Pre-deployment Steps

### 1. Environment Setup
- [ ] Copy `docker/.env.example` to `docker/.env`
- [ ] Configure database credentials in `.env`
- [ ] Set OpenAQ API key (optional but recommended)
- [ ] Configure Twilio credentials for SMS alerts (optional)
- [ ] Set secure passwords for all services

### 2. Security Configuration
- [ ] Change default database passwords
- [ ] Configure SSL certificates for HTTPS
- [ ] Review CORS settings for production domains
- [ ] Set up rate limiting rules
- [ ] Configure firewall rules

### 3. Resource Planning
- [ ] Allocate sufficient memory (minimum 2GB recommended)
- [ ] Plan disk space for database and logs
- [ ] Configure backup strategy
- [ ] Set up monitoring and alerting

## Deployment Commands

### Using Deployment Scripts
```bash
# Windows
scripts\deploy.bat

# Linux/Mac
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### Manual Deployment
```bash
# Build and start services
docker-compose -f docker/docker-compose.yml up -d

# View logs
docker-compose -f docker/docker-compose.yml logs -f

# Check health
curl http://localhost:8080/api/health
```

## Post-deployment Verification

### Health Checks
- [ ] Application health: `curl http://localhost:8080/api/health`
- [ ] Database connectivity: Check health endpoint response
- [ ] Frontend loading: Visit `http://localhost:8080`
- [ ] API endpoints: Test `/api/aqi/cities`

### Functional Tests
- [ ] City search and data retrieval
- [ ] User registration and login
- [ ] Alert system (if SMS configured)
- [ ] PDF/CSV export (for registered users)
- [ ] Real-time data updates

### Performance Verification
- [ ] Response times under 2 seconds
- [ ] Memory usage stable under load
- [ ] Database connection pool healthy
- [ ] No memory leaks in extended running

## Monitoring Setup

### Application Metrics
- Monitor `/api/health` endpoint
- Track response times and error rates
- Monitor memory and CPU usage
- Database connection pool metrics

### Alerting
- Set up alerts for service downtime
- Monitor disk space usage
- Database connection failures
- External API rate limit exceeded

## Maintenance

### Regular Tasks
- [ ] Database backup (daily/weekly)
- [ ] Log rotation and cleanup
- [ ] Update dependencies monthly
- [ ] Review and update SSL certificates
- [ ] Monitor and clean old data (90 days retention)

### Scaling Considerations
- [ ] Database read replicas for high load
- [ ] Application horizontal scaling
- [ ] CDN for static assets
- [ ] Load balancer configuration
- [ ] Auto-scaling policies

## Troubleshooting

### Common Issues
1. **Port conflicts**: Change ports in docker/.env
2. **Database connection**: Verify credentials and network
3. **API rate limits**: Add OpenAQ API key
4. **Memory issues**: Increase container limits
5. **SSL certificate**: Check certificate validity and paths

### Debug Commands
```bash
# View container logs
docker-compose logs -f [service_name]

# Access application container
docker-compose exec app bash

# Access database
docker-compose exec mysql mysql -u root -p

# Check container resource usage
docker stats
```

## Security Hardening

### Production Security
- [ ] Use strong, unique passwords
- [ ] Enable SSL/TLS encryption
- [ ] Configure proper firewall rules
- [ ] Regular security updates
- [ ] Monitor access logs
- [ ] Implement intrusion detection
- [ ] Regular security audits

### Network Security
- [ ] Use private networks for containers
- [ ] Restrict database access to application only
- [ ] Configure reverse proxy security headers
- [ ] Implement rate limiting
- [ ] Monitor for suspicious activities

## Backup and Recovery

### Backup Strategy
- [ ] Automated database backups
- [ ] Application configuration backups
- [ ] SSL certificates and keys backup
- [ ] Test restore procedures
- [ ] Off-site backup storage

### Disaster Recovery
- [ ] Document recovery procedures
- [ ] Test disaster recovery plan
- [ ] Maintain updated deployment documentation
- [ ] Keep emergency contact information
- [ ] Regular recovery testing

---

**AirSight Production Deployment** - Follow this checklist for successful production deployment
