# Terraform configuration for AirSight on Google Cloud Platform
# This configuration creates all necessary infrastructure for AirSight deployment

terraform {
  required_version = ">= 1.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
    google-beta = {
      source  = "hashicorp/google-beta"
      version = "~> 4.0"
    }
  }
}

# Variables
variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "GCP Region"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "GCP Zone"
  type        = string
  default     = "us-central1-a"
}

variable "domain_name" {
  description = "Custom domain name (optional)"
  type        = string
  default     = ""
}

variable "db_instance_tier" {
  description = "Cloud SQL instance tier"
  type        = string
  default     = "db-f1-micro"
}

# Provider configuration
provider "google" {
  project = var.project_id
  region  = var.region
}

provider "google-beta" {
  project = var.project_id
  region  = var.region
}

# Enable required APIs
resource "google_project_service" "required_apis" {
  for_each = toset([
    "cloudbuild.googleapis.com",
    "run.googleapis.com",
    "sqladmin.googleapis.com",
    "secretmanager.googleapis.com",
    "container.googleapis.com",
    "redis.googleapis.com",
    "monitoring.googleapis.com",
    "logging.googleapis.com"
  ])
  
  project = var.project_id
  service = each.key
  
  disable_on_destroy = false
}

# Cloud SQL Instance
resource "google_sql_database_instance" "airsight_mysql" {
  name             = "airsight-mysql"
  database_version = "MYSQL_8_0"
  region           = var.region
  
  settings {
    tier              = var.db_instance_tier
    disk_size         = 20
    disk_type         = "PD_SSD"
    disk_autoresize   = true
    availability_type = "ZONAL"
    
    backup_configuration {
      enabled    = true
      start_time = "02:00"
    }
    
    maintenance_window {
      day  = 7
      hour = 3
    }
    
    ip_configuration {
      ipv4_enabled = true
    }
  }
  
  deletion_protection = false
  
  depends_on = [google_project_service.required_apis]
}

# Database
resource "google_sql_database" "airsight_db" {
  name     = "airqualitydb"
  instance = google_sql_database_instance.airsight_mysql.name
}

# Database user
resource "google_sql_user" "airsight_user" {
  name     = "airsight_user"
  instance = google_sql_database_instance.airsight_mysql.name
  password = random_password.db_password.result
}

# Random password for database user
resource "random_password" "db_password" {
  length  = 16
  special = true
}

# Secret Manager secrets
resource "google_secret_manager_secret" "db_password" {
  secret_id = "db-password"
  
  replication {
    automatic = true
  }
  
  depends_on = [google_project_service.required_apis]
}

resource "google_secret_manager_secret_version" "db_password" {
  secret      = google_secret_manager_secret.db_password.id
  secret_data = random_password.db_password.result
}

# OpenAQ API Key secret
resource "google_secret_manager_secret" "openaq_api_key" {
  secret_id = "openaq-api-key"
  
  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "openaq_api_key" {
  secret      = google_secret_manager_secret.openaq_api_key.id
  secret_data = "your-openaq-api-key-here"
}

# Twilio secrets
resource "google_secret_manager_secret" "twilio_account_sid" {
  secret_id = "twilio-account-sid"
  
  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "twilio_account_sid" {
  secret      = google_secret_manager_secret.twilio_account_sid.id
  secret_data = "your-twilio-account-sid"
}

resource "google_secret_manager_secret" "twilio_auth_token" {
  secret_id = "twilio-auth-token"
  
  replication {
    automatic = true
  }
}

resource "google_secret_manager_secret_version" "twilio_auth_token" {
  secret      = google_secret_manager_secret.twilio_auth_token.id
  secret_data = "your-twilio-auth-token"
}

# Memorystore Redis instance
resource "google_redis_instance" "airsight_redis" {
  name           = "airsight-redis"
  tier           = "BASIC"
  memory_size_gb = 1
  region         = var.region
  
  redis_version = "REDIS_6_X"
  display_name  = "AirSight Redis Cache"
  
  depends_on = [google_project_service.required_apis]
}

# Service account for Cloud Run
resource "google_service_account" "airsight_service_account" {
  account_id   = "airsight-service"
  display_name = "AirSight Service Account"
  description  = "Service account for AirSight application"
}

# IAM binding for Secret Manager
resource "google_secret_manager_secret_iam_binding" "db_password_access" {
  secret_id = google_secret_manager_secret.db_password.secret_id
  role      = "roles/secretmanager.secretAccessor"
  
  members = [
    "serviceAccount:${google_service_account.airsight_service_account.email}",
  ]
}

resource "google_secret_manager_secret_iam_binding" "openaq_api_key_access" {
  secret_id = google_secret_manager_secret.openaq_api_key.secret_id
  role      = "roles/secretmanager.secretAccessor"
  
  members = [
    "serviceAccount:${google_service_account.airsight_service_account.email}",
  ]
}

# IAM binding for Cloud SQL
resource "google_project_iam_binding" "airsight_cloudsql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  
  members = [
    "serviceAccount:${google_service_account.airsight_service_account.email}",
  ]
}

# Cloud Run service
resource "google_cloud_run_service" "airsight" {
  name     = "airsight"
  location = var.region
  
  template {
    metadata {
      annotations = {
        "autoscaling.knative.dev/maxScale"        = "10"
        "autoscaling.knative.dev/minScale"        = "1"
        "run.googleapis.com/cloudsql-instances"   = google_sql_database_instance.airsight_mysql.connection_name
        "run.googleapis.com/cpu-throttling"       = "false"
      }
    }
    
    spec {
      container_concurrency = 80
      timeout_seconds      = 300
      service_account_name = google_service_account.airsight_service_account.email
      
      containers {
        image = "gcr.io/${var.project_id}/airsight:latest"
        
        ports {
          container_port = 8080
        }
        
        env {
          name  = "ENVIRONMENT"
          value = "production"
        }
        
        env {
          name  = "DATABASE_URL"
          value = "jdbc:mysql://127.0.0.1:3306/airqualitydb?cloudSqlInstance=${google_sql_database_instance.airsight_mysql.connection_name}&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false"
        }
        
        env {
          name  = "DB_USERNAME"
          value = google_sql_user.airsight_user.name
        }
        
        env {
          name = "DB_PASSWORD"
          value_from {
            secret_key_ref {
              name = google_secret_manager_secret.db_password.secret_id
              key  = "latest"
            }
          }
        }
        
        env {
          name  = "REDIS_HOST"
          value = google_redis_instance.airsight_redis.host
        }
        
        env {
          name  = "REDIS_PORT"
          value = "6379"
        }
        
        env {
          name  = "GOOGLE_CLOUD_LOGGING_ENABLED"
          value = "true"
        }
        
        env {
          name  = "GOOGLE_CLOUD_MONITORING_ENABLED"
          value = "true"
        }
        
        resources {
          limits = {
            cpu    = "2000m"
            memory = "2Gi"
          }
          requests = {
            cpu    = "1000m"
            memory = "1Gi"
          }
        }
      }
    }
  }
  
  traffic {
    percent         = 100
    latest_revision = true
  }
  
  depends_on = [
    google_project_service.required_apis,
    google_sql_database_instance.airsight_mysql,
    google_redis_instance.airsight_redis
  ]
}

# Make Cloud Run service publicly accessible
resource "google_cloud_run_service_iam_binding" "airsight_noauth" {
  location = google_cloud_run_service.airsight.location
  project  = google_cloud_run_service.airsight.project
  service  = google_cloud_run_service.airsight.name
  role     = "roles/run.invoker"
  
  members = [
    "allUsers",
  ]
}

# Cloud Build trigger (optional)
resource "google_cloudbuild_trigger" "airsight_trigger" {
  name        = "airsight-deploy"
  description = "Trigger for AirSight deployment"
  
  github {
    owner = "harshith-varma07"
    name  = "AirSight"
    push {
      branch = "main"
    }
  }
  
  filename = "cloudbuild.yaml"
  
  depends_on = [google_project_service.required_apis]
}

# Outputs
output "cloud_run_url" {
  description = "URL of the Cloud Run service"
  value       = google_cloud_run_service.airsight.status[0].url
}

output "database_connection_name" {
  description = "Cloud SQL instance connection name"
  value       = google_sql_database_instance.airsight_mysql.connection_name
}

output "database_ip" {
  description = "Cloud SQL instance IP address"
  value       = google_sql_database_instance.airsight_mysql.ip_address.0.ip_address
}

output "redis_host" {
  description = "Redis instance host"
  value       = google_redis_instance.airsight_redis.host
}

output "service_account_email" {
  description = "Service account email"
  value       = google_service_account.airsight_service_account.email
}