# Deployment Guide

This document provides comprehensive deployment instructions for the Smart Budget App across different environments.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Environment Configuration](#environment-configuration)
- [Deployment Methods](#deployment-methods)
- [Infrastructure Requirements](#infrastructure-requirements)
- [Database Setup](#database-setup)
- [Monitoring and Health Checks](#monitoring-and-health-checks)
- [Backup and Restore](#backup-and-restore)
- [Troubleshooting](#troubleshooting)

## Prerequisites

- Docker 24.0+ and Docker Compose 2.20+
- PostgreSQL 15+ (if not using Docker)
- Node.js 20+ LTS (for local frontend development)
- Java 21 (for local backend development)
- Git

## Environment Configuration

### Required Environment Variables

All environments require the following environment variables:

```bash
# Application Profile
SPRING_PROFILES_ACTIVE=dev|staging|prod

# Database Connection
DATABASE_URL=jdbc:postgresql://hostname:5432/database_name
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters-long

# CORS Configuration
ALLOWED_ORIGINS=https://your-frontend-domain.com

# Optional: Server Port
SERVER_PORT=8080
```

### Generating Secure JWT Secret

For production, generate a strong random secret key:

```bash
# Using OpenSSL
openssl rand -base64 64

# Using Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

### Environment-Specific Configuration

#### Development

```bash
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://localhost:5432/smartbudget
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=dev-secret-key-for-jwt-token-signing-must-be-256-bits-minimum-do-not-use-in-production
ALLOWED_ORIGINS=http://localhost:4200,http://localhost:80
```

#### Staging

```bash
SPRING_PROFILES_ACTIVE=staging
DATABASE_URL=jdbc:postgresql://staging-db.example.com:5432/smartbudget_staging
DATABASE_USERNAME=smartbudget_staging
DATABASE_PASSWORD=<SECURE_PASSWORD>
JWT_SECRET=<SECURE_256_BIT_SECRET>
ALLOWED_ORIGINS=https://staging.smartbudget.example.com
```

#### Production

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://prod-db.example.com:5432/smartbudget_prod
DATABASE_USERNAME=smartbudget_prod
DATABASE_PASSWORD=<SECURE_PASSWORD>
JWT_SECRET=<SECURE_256_BIT_SECRET>
ALLOWED_ORIGINS=https://smartbudget.example.com
```

## Deployment Methods

### Docker Compose (Recommended for Development/Staging)

1. **Clone the repository:**

```bash
git clone https://github.com/your-org/smart-budget-app.git
cd smart-budget-app
```

2. **Create environment file:**

```bash
cp .env.example .env
# Edit .env with your environment variables
```

3. **Build and start services:**

```bash
# Build images
docker-compose build

# Start all services (detached mode)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

4. **Verify deployment:**

```bash
# Check backend health
curl http://localhost:8080/api/health

# Check frontend
curl http://localhost/
```

### Docker (Production)

For production, deploy containers individually with orchestration (Kubernetes, AWS ECS, etc.):

1. **Build production images:**

```bash
# Backend
cd backend
docker build -t smartbudget-backend:latest .

# Frontend
cd ../frontend
docker build -t smartbudget-frontend:latest .
```

2. **Run containers:**

```bash
# PostgreSQL
docker run -d \
  --name smartbudget-db \
  -e POSTGRES_DB=smartbudget \
  -e POSTGRES_USER=smartbudget \
  -e POSTGRES_PASSWORD=<SECURE_PASSWORD> \
  -v smartbudget-data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:15-alpine

# Backend
docker run -d \
  --name smartbudget-backend \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://smartbudget-db:5432/smartbudget \
  -e DATABASE_USERNAME=smartbudget \
  -e DATABASE_PASSWORD=<SECURE_PASSWORD> \
  -e JWT_SECRET=<SECURE_256_BIT_SECRET> \
  -e ALLOWED_ORIGINS=https://smartbudget.example.com \
  --link smartbudget-db \
  -p 8080:8080 \
  smartbudget-backend:latest

# Frontend
docker run -d \
  --name smartbudget-frontend \
  --link smartbudget-backend \
  -p 80:8080 \
  smartbudget-frontend:latest
```

### Cloud Platform Deployments

#### AWS (Elastic Container Service)

1. **Push images to ECR:**

```bash
# Authenticate to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push images
docker tag smartbudget-backend:latest <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/smartbudget-backend:latest
docker push <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/smartbudget-backend:latest

docker tag smartbudget-frontend:latest <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/smartbudget-frontend:latest
docker push <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/smartbudget-frontend:latest
```

2. **Create RDS PostgreSQL database**
3. **Create ECS task definitions** with environment variables from Secrets Manager
4. **Deploy to ECS cluster** with Application Load Balancer

#### Azure (Container Apps)

```bash
# Create resource group
az group create --name smartbudget-rg --location eastus

# Create PostgreSQL server
az postgres flexible-server create \
  --resource-group smartbudget-rg \
  --name smartbudget-db \
  --location eastus \
  --admin-user smartbudget \
  --admin-password <SECURE_PASSWORD> \
  --version 15

# Deploy backend container
az containerapp create \
  --name smartbudget-backend \
  --resource-group smartbudget-rg \
  --image smartbudget-backend:latest \
  --target-port 8080 \
  --env-vars SPRING_PROFILES_ACTIVE=prod DATABASE_URL=<CONNECTION_STRING> \
  --ingress external

# Deploy frontend container
az containerapp create \
  --name smartbudget-frontend \
  --resource-group smartbudget-rg \
  --image smartbudget-frontend:latest \
  --target-port 8080 \
  --ingress external
```

#### Google Cloud Platform (Cloud Run)

```bash
# Push to Google Container Registry
gcloud auth configure-docker
docker tag smartbudget-backend:latest gcr.io/<PROJECT_ID>/smartbudget-backend:latest
docker push gcr.io/<PROJECT_ID>/smartbudget-backend:latest

# Deploy backend
gcloud run deploy smartbudget-backend \
  --image gcr.io/<PROJECT_ID>/smartbudget-backend:latest \
  --platform managed \
  --region us-central1 \
  --set-env-vars SPRING_PROFILES_ACTIVE=prod,DATABASE_URL=<CONNECTION_STRING>

# Deploy frontend
docker tag smartbudget-frontend:latest gcr.io/<PROJECT_ID>/smartbudget-frontend:latest
docker push gcr.io/<PROJECT_ID>/smartbudget-frontend:latest

gcloud run deploy smartbudget-frontend \
  --image gcr.io/<PROJECT_ID>/smartbudget-frontend:latest \
  --platform managed \
  --region us-central1
```

## Infrastructure Requirements

### Minimum Requirements

| Component | CPU | Memory | Storage | Notes |
|-----------|-----|--------|---------|-------|
| Frontend | 0.5 vCPU | 512 MB | 100 MB | NGINX serving static files |
| Backend | 1 vCPU | 1 GB | 500 MB | Spring Boot application |
| Database | 1 vCPU | 2 GB | 20 GB | PostgreSQL with connection pooling |

### Recommended Production Requirements

| Component | CPU | Memory | Storage | Notes |
|-----------|-----|--------|---------|-------|
| Frontend | 1 vCPU | 1 GB | 100 MB | Multiple replicas for HA |
| Backend | 2 vCPU | 4 GB | 1 GB | Auto-scaling 2-10 instances |
| Database | 2 vCPU | 8 GB | 100 GB SSD | Managed service (RDS, Cloud SQL) |

### Network Configuration

- **Backend:** Port 8080 (HTTP)
- **Frontend:** Port 8080 (HTTP, served by NGINX)
- **Database:** Port 5432 (PostgreSQL)
- **SSL/TLS:** Terminate at load balancer or reverse proxy

## Database Setup

### Initial Database Creation

```sql
-- Create database
CREATE DATABASE smartbudget;

-- Create user
CREATE USER smartbudget WITH PASSWORD '<SECURE_PASSWORD>';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE smartbudget TO smartbudget;
```

### Schema Migrations

Schema migrations are handled automatically by Flyway on application startup.

**Migration files location:** `backend/src/main/resources/db/migration/`

**Manual migration:**

```bash
# Using Gradle
cd backend
./gradlew flywayMigrate

# Using Docker
docker exec smartbudget-backend ./gradlew flywayMigrate
```

### Database Connection Pooling

Production configuration uses HikariCP with the following settings:

- **Maximum pool size:** 20 connections
- **Minimum idle:** 10 connections
- **Connection timeout:** 30 seconds
- **Idle timeout:** 10 minutes
- **Max lifetime:** 30 minutes

## Monitoring and Health Checks

### Health Check Endpoint

**URL:** `GET /api/health`

**Response (healthy):**

```json
{
  "status": "UP",
  "timestamp": "2025-11-16T10:30:00Z",
  "version": "1.0.0",
  "database": "UP"
}
```

**Response (unhealthy):**

```json
{
  "status": "DOWN",
  "timestamp": "2025-11-16T10:30:00Z",
  "version": "1.0.0",
  "database": "DOWN"
}
```

HTTP Status: 503 Service Unavailable

### Docker Health Checks

Health checks are configured in Dockerfiles:

- **Backend:** Checks `/api/health` every 30s
- **Frontend:** Checks homepage every 30s

### Load Balancer Health Checks

Configure your load balancer to check `/api/health` (backend) and `/` (frontend).

**Recommended settings:**
- Interval: 30 seconds
- Timeout: 5 seconds
- Healthy threshold: 2 consecutive successes
- Unhealthy threshold: 3 consecutive failures

## Backup and Restore

### Database Backup

**Automated daily backup (cron):**

```bash
#!/bin/bash
# backup-db.sh
BACKUP_DIR="/backups/smartbudget"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/smartbudget_$DATE.sql"

mkdir -p $BACKUP_DIR

docker exec smartbudget-db pg_dump -U smartbudget smartbudget > $BACKUP_FILE

# Compress backup
gzip $BACKUP_FILE

# Delete backups older than 30 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Backup completed: $BACKUP_FILE.gz"
```

**Manual backup:**

```bash
# Using Docker
docker exec smartbudget-db pg_dump -U smartbudget smartbudget > backup.sql

# Direct connection
pg_dump -h hostname -U smartbudget smartbudget > backup.sql
```

### Database Restore

```bash
# Stop application
docker-compose down backend

# Restore database
docker exec -i smartbudget-db psql -U smartbudget smartbudget < backup.sql

# Or with gzip
gunzip -c backup.sql.gz | docker exec -i smartbudget-db psql -U smartbudget smartbudget

# Restart application
docker-compose up -d backend
```

## Troubleshooting

### Application won't start

**Check logs:**

```bash
# Docker Compose
docker-compose logs backend
docker-compose logs frontend

# Docker
docker logs smartbudget-backend
docker logs smartbudget-frontend
```

**Common issues:**

1. **Database connection failed**
   - Verify DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD
   - Check database is running: `docker ps | grep postgres`
   - Test connection: `psql -h localhost -U smartbudget -d smartbudget`

2. **JWT secret not configured**
   - Error: "JWT_SECRET environment variable not set"
   - Solution: Set JWT_SECRET environment variable

3. **CORS errors in browser**
   - Verify ALLOWED_ORIGINS matches frontend URL
   - Check browser console for specific origin

### Database migration failed

```bash
# Check Flyway migration status
docker exec smartbudget-backend ./gradlew flywayInfo

# Repair failed migration
docker exec smartbudget-backend ./gradlew flywayRepair

# Baseline existing database (caution!)
docker exec smartbudget-backend ./gradlew flywayBaseline
```

### High memory usage

**Backend:**
- Check JVM heap size configuration
- Review connection pool settings
- Monitor for memory leaks with heap dumps

**Frontend:**
- NGINX typically uses <50MB
- Check for excessive logging

### Slow database queries

```sql
-- Enable query logging
ALTER DATABASE smartbudget SET log_statement = 'all';
ALTER DATABASE smartbudget SET log_duration = 'on';

-- Find slow queries
SELECT query, mean_exec_time, calls
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

### Container health check failing

```bash
# Check health status
docker inspect smartbudget-backend | grep Health -A 10

# Manual health check
curl http://localhost:8080/api/health

# Check database connectivity from container
docker exec smartbudget-backend pg_isready -h smartbudget-db -U smartbudget
```

## Security Checklist

Before production deployment:

- [ ] JWT_SECRET is a strong random 256-bit key
- [ ] Database passwords are strong and stored in secrets manager
- [ ] ALLOWED_ORIGINS is set to specific domain (no wildcards)
- [ ] SSL/TLS is enabled (terminate at load balancer or app level)
- [ ] Database is not publicly accessible
- [ ] Containers run as non-root users
- [ ] Security headers are configured in NGINX
- [ ] Database backups are automated and tested
- [ ] Monitoring and alerting are configured
- [ ] Secrets are not committed to version control

## Support

For deployment issues:
- Check logs first: `docker-compose logs -f`
- Review this troubleshooting section
- Check GitHub Issues: https://github.com/your-org/smart-budget-app/issues
