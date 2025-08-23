# Terraform Infrastructure Deployment for AirSight

This directory contains Terraform configuration files to deploy AirSight infrastructure on Google Cloud Platform.

## Prerequisites

1. Install Terraform (>= 1.0)
2. Install Google Cloud SDK
3. Authenticate with Google Cloud:
   ```bash
   gcloud auth login
   gcloud auth application-default login
   ```

## Quick Start

1. **Copy and update variables**:
   ```bash
   cp terraform.tfvars.example terraform.tfvars
   # Edit terraform.tfvars with your project ID and preferences
   ```

2. **Initialize Terraform**:
   ```bash
   terraform init
   ```

3. **Plan deployment**:
   ```bash
   terraform plan
   ```

4. **Apply infrastructure**:
   ```bash
   terraform apply
   ```

## What gets created

- **Cloud SQL MySQL instance** with database and user
- **Memorystore Redis instance** for caching
- **Secret Manager secrets** for sensitive data
- **Service account** with required permissions
- **Cloud Run service** (placeholder - update image after building)
- **Cloud Build trigger** for CI/CD

## Post-deployment steps

1. Build and deploy your application:
   ```bash
   # Build and push Docker image
   gcloud builds submit --config=../cloudbuild.yaml
   ```

2. Update application secrets in Secret Manager:
   ```bash
   # Update OpenAQ API key
   echo "your-actual-api-key" | gcloud secrets versions add openaq-api-key --data-file=-
   
   # Update Twilio credentials (if using SMS alerts)
   echo "your-twilio-sid" | gcloud secrets versions add twilio-account-sid --data-file=-
   echo "your-twilio-token" | gcloud secrets versions add twilio-auth-token --data-file=-
   ```

3. Initialize database schema:
   ```bash
   # Upload database schema to Cloud Storage
   gsutil cp ../database_setup.sql gs://your-bucket/
   
   # Import schema
   gcloud sql import sql airsight-mysql gs://your-bucket/database_setup.sql \
       --database=airqualitydb
   ```

## Outputs

After successful deployment, Terraform will output:
- **cloud_run_url**: URL of your deployed application
- **database_connection_name**: Cloud SQL connection string
- **redis_host**: Redis instance hostname
- **service_account_email**: Service account for the application

## Cleanup

To destroy all created resources:
```bash
terraform destroy
```

## Cost Optimization

The default configuration uses:
- **db-f1-micro**: Smallest Cloud SQL tier (~$7/month)
- **1GB Redis**: Basic tier (~$30/month)
- **Cloud Run**: Pay-per-use (~$5-50/month depending on traffic)

Total estimated cost: **$42-87/month** for low to medium traffic.