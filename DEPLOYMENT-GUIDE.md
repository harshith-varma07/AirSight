# 🚀 AirSight Google Cloud Platform Deployment Guide

This comprehensive guide provides step-by-step instructions for deploying AirSight on Google Cloud Platform using various services including Cloud Run, App Engine, and Google Kubernetes Engine (GKE).

## 📋 Prerequisites

### Required Tools
- Google Cloud SDK (`gcloud` CLI) installed and authenticated
- Docker installed locally
- Git for cloning the repository
- Java 17+ for local testing

### Google Cloud Setup
1. **Create or select a Google Cloud Project**
   ```bash
   # Create a new project
   gcloud projects create airsight-prod --name="AirSight Production"
   
   # Set as default project
   gcloud config set project airsight-prod
   ```

2. **Enable required APIs**
   ```bash
   gcloud services enable \
     cloudbuild.googleapis.com \
     run.googleapis.com \
     sqladmin.googleapis.com \
     secretmanager.googleapis.com \
     container.googleapis.com \
     appengine.googleapis.com
   ```

3. **Set up authentication**
   ```bash
   gcloud auth login
   gcloud auth application-default login
   ```

## 🏗️ Architecture Overview

```
Internet → Cloud Load Balancer → [Cloud Run/GKE/App Engine]
                                           ↓
                                    Cloud SQL (MySQL)
                                           ↓
                                    Memorystore (Redis)
```

## 🗄️ Database Setup (Cloud SQL)

### 1. Create Cloud SQL Instance
```bash
# Create MySQL instance
gcloud sql instances create airsight-mysql \
    --database-version=MYSQL_8_0 \
    --tier=db-f1-micro \
    --region=us-central1 \
    --storage-size=20GB \
    --storage-type=SSD \
    --storage-auto-increase \
    --backup-start-time=02:00 \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=03 \
    --enable-bin-log

# Set root password
gcloud sql users set-password root \
    --host=% \
    --instance=airsight-mysql \
    --password='your-secure-root-password'

# Create application database
gcloud sql databases create airqualitydb --instance=airsight-mysql

# Create application user
gcloud sql users create airsight_user \
    --host=% \
    --instance=airsight-mysql \
    --password='your-secure-user-password'
```

### 2. Initialize Database Schema
```bash
# Import database schema
gcloud sql import sql airsight-mysql gs://your-bucket/database_setup.sql \
    --database=airqualitydb
```

## 🔐 Secrets Management

Store sensitive configuration in Google Cloud Secret Manager:

```bash
# Create secrets
gcloud secrets create db-password --data-file=- <<< "your-secure-user-password"
gcloud secrets create openaq-api-key --data-file=- <<< "your-openaq-api-key"
gcloud secrets create twilio-account-sid --data-file=- <<< "your-twilio-sid"
gcloud secrets create twilio-auth-token --data-file=- <<< "your-twilio-token"
gcloud secrets create twilio-phone-number --data-file=- <<< "+1234567890"

# Grant access to compute service account
PROJECT_ID=$(gcloud config get-value project)
PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format='value(projectNumber)')

gcloud secrets add-iam-policy-binding db-password \
    --member="serviceAccount:$PROJECT_NUMBER-compute@developer.gserviceaccount.com" \
    --role="roles/secretmanager.secretAccessor"
```

## 🚀 Deployment Options

### Option 1: Cloud Run (Recommended for simplicity)

Cloud Run provides serverless container deployment with automatic scaling and pay-per-use billing.

#### 1. Build and Deploy
```bash
# Clone the repository
git clone https://github.com/harshith-varma07/AirSight.git
cd AirSight

# Generate environment configuration
./scripts/generate-env.sh

# Update GCP-specific configuration
cp docker/.env.gcp docker/.env
# Edit docker/.env with your project details

# Deploy using Cloud Build
gcloud builds submit --config=cloudbuild.yaml

# Alternative: Manual build and deploy
# Build locally
docker build -f docker/Dockerfile -t gcr.io/$PROJECT_ID/airsight .

# Push to Container Registry
docker push gcr.io/$PROJECT_ID/airsight

# Deploy to Cloud Run
gcloud run deploy airsight \
    --image gcr.io/$PROJECT_ID/airsight \
    --region us-central1 \
    --platform managed \
    --allow-unauthenticated \
    --port 8080 \
    --memory 2Gi \
    --cpu 2 \
    --min-instances 1 \
    --max-instances 10 \
    --set-cloudsql-instances $PROJECT_ID:us-central1:airsight-mysql \
    --set-env-vars ENVIRONMENT=production,TZ=UTC
```

#### 2. Configure Custom Domain (Optional)
```bash
# Map custom domain
gcloud run domain-mappings create \
    --service airsight \
    --domain your-domain.com \
    --region us-central1
```

### Option 2: App Engine

App Engine provides a fully managed platform with integrated services.

#### 1. Deploy to App Engine
```bash
# Initialize App Engine
gcloud app create --region=us-central1

# Update app.yaml with your project details
sed -i 's/PROJECT_ID/'$PROJECT_ID'/g' app.yaml
sed -i 's/REGION/us-central1/g' app.yaml
sed -i 's/INSTANCE_NAME/airsight-mysql/g' app.yaml

# Deploy application
gcloud app deploy app.yaml --quiet
```

### Option 3: Google Kubernetes Engine (GKE)

GKE provides full Kubernetes orchestration with advanced scaling and management features.

#### 1. Create GKE Cluster
```bash
# Create cluster
gcloud container clusters create airsight-cluster \
    --zone us-central1-a \
    --num-nodes 2 \
    --machine-type e2-standard-2 \
    --enable-autoscaling \
    --min-nodes 1 \
    --max-nodes 5 \
    --enable-autorepair \
    --enable-autoupgrade \
    --scopes https://www.googleapis.com/auth/cloud-platform

# Get credentials
gcloud container clusters get-credentials airsight-cluster --zone us-central1-a
```

#### 2. Deploy to GKE
```bash
# Update manifest with your project details
sed -i 's/PROJECT_ID/'$PROJECT_ID'/g' k8s-manifest.yaml
sed -i 's/REGION/us-central1/g' k8s-manifest.yaml
sed -i 's/INSTANCE_NAME/airsight-mysql/g' k8s-manifest.yaml

# Create Cloud SQL service account key
gcloud iam service-accounts create cloudsql-proxy
gcloud iam service-accounts keys create key.json \
    --iam-account cloudsql-proxy@$PROJECT_ID.iam.gserviceaccount.com

# Grant Cloud SQL permissions
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member serviceAccount:cloudsql-proxy@$PROJECT_ID.iam.gserviceaccount.com \
    --role roles/cloudsql.client

# Create secret for Cloud SQL proxy
kubectl create secret generic cloudsql-instance-credentials \
    --from-file=key.json=key.json -n airsight

# Deploy application
kubectl apply -f k8s-manifest.yaml

# Get external IP
kubectl get ingress airsight-ingress -n airsight
```

## 🔧 Configuration Management

### Environment Variables
The deployment automatically configures the following from your application properties:

- **Database Configuration**: Extracted from `spring.datasource.*` properties
- **Application Settings**: Server port, logging levels, performance tuning
- **External APIs**: OpenAQ and Twilio configurations
- **Google Cloud Integration**: Cloud SQL, Secret Manager, monitoring

### Automatic Configuration Generation
Use the provided script to generate environment files:
```bash
# Generate standard and GCP-specific configurations
./scripts/generate-env.sh

# Files generated:
# - docker/.env - Standard deployment configuration
# - docker/.env.gcp - Google Cloud Platform specific configuration
```

## 📊 Monitoring and Logging

### 1. Enable Google Cloud Monitoring
```bash
# Enable monitoring for your service
gcloud run services update airsight \
    --region us-central1 \
    --set-env-vars GOOGLE_CLOUD_MONITORING_ENABLED=true
```

### 2. View Logs
```bash
# Cloud Run logs
gcloud logs read "resource.type=cloud_run_revision" --limit 50

# App Engine logs
gcloud logs read "resource.type=gae_app" --limit 50

# GKE logs
gcloud logs read "resource.type=k8s_container" --limit 50
```

### 3. Set up Alerting
```bash
# Create alerting policy for high error rate
gcloud alpha monitoring policies create --policy-from-file=monitoring-policy.yaml
```

## 💰 Cost Optimization

### Cloud Run Pricing
- **CPU**: $0.00002400 per vCPU-second
- **Memory**: $0.00000250 per GiB-second
- **Requests**: $0.40 per million requests
- **Minimum**: 1 instance, maximum: 10 instances

### Estimated Monthly Costs
- **Low Traffic** (1K requests/day): ~$5-10/month
- **Medium Traffic** (10K requests/day): ~$15-30/month
- **High Traffic** (100K requests/day): ~$50-100/month

*Note: Costs include Cloud SQL ($7-15/month for db-f1-micro) and minimal Memorystore usage.*

## 🔄 CI/CD Pipeline

### 1. Set up Cloud Build Triggers
```bash
# Connect repository
gcloud builds triggers create github \
    --repo-name=AirSight \
    --repo-owner=harshith-varma07 \
    --branch-pattern="^main$" \
    --build-config=cloudbuild.yaml
```

### 2. Automated Deployment
The `cloudbuild.yaml` configuration provides:
- Automated testing and building
- Container image creation and publishing
- Deployment to Cloud Run
- Environment-specific configuration

## 🛠️ Maintenance and Updates

### Application Updates
```bash
# Deploy new version
gcloud builds submit --config=cloudbuild.yaml

# Rollback if needed
gcloud run services update airsight \
    --image gcr.io/$PROJECT_ID/airsight:previous-build-id \
    --region us-central1
```

### Database Maintenance
```bash
# Create backup
gcloud sql backups create \
    --instance=airsight-mysql \
    --description="Manual backup before update"

# Apply maintenance updates
gcloud sql instances patch airsight-mysql \
    --maintenance-window-day=SUN \
    --maintenance-window-hour=3
```

## 🚨 Troubleshooting

### Common Issues

1. **Cloud SQL Connection Issues**
   ```bash
   # Check Cloud SQL proxy logs
   gcloud logs read "resource.type=cloud_run_revision" --filter="textPayload:cloud_sql_proxy"
   
   # Verify instance connection name
   gcloud sql instances describe airsight-mysql --format="value(connectionName)"
   ```

2. **Memory Issues**
   ```bash
   # Increase memory allocation
   gcloud run services update airsight \
       --memory 4Gi \
       --region us-central1
   ```

3. **Cold Start Performance**
   ```bash
   # Set minimum instances
   gcloud run services update airsight \
       --min-instances 2 \
       --region us-central1
   ```

### Health Checks
```bash
# Test application health
curl https://your-service-url.run.app/api/health

# Check service status
gcloud run services describe airsight --region us-central1
```

## 📞 Support and Resources

- **Google Cloud Documentation**: [Cloud Run](https://cloud.google.com/run/docs), [App Engine](https://cloud.google.com/appengine/docs), [GKE](https://cloud.google.com/kubernetes-engine/docs)
- **AirSight Issues**: [GitHub Issues](https://github.com/harshith-varma07/AirSight/issues)
- **Google Cloud Support**: Available with paid support plans

---

🎉 **Congratulations!** Your AirSight application is now running on Google Cloud Platform with enterprise-grade scalability, monitoring, and security.