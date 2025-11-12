# Deployment Architecture

## Containerization

**Frontend (Dockerfile.frontend):**
```dockerfile
# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build -- --configuration production

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Backend (Dockerfile.backend):**
```dockerfile
# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/gradle ./gradle
COPY backend/src ./src
RUN gradle build -x test --no-daemon

# Production stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Local Development (docker-compose.yml)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: smartbudget
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/smartbudget
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      JWT_SECRET: ${JWT_SECRET:-dev-secret-key-change-in-production}
      SPRING_PROFILES_ACTIVE: dev
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    volumes:
      - ./backend/src:/app/src:ro

  frontend:
    build:
      context: .
      dockerfile: Dockerfile.frontend
    environment:
      API_URL: http://localhost:8080/api
    ports:
      - "4200:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

## CI/CD Pipeline (GitHub Actions)

**.github/workflows/ci.yml:**
```yaml
name: CI

on:
  pull_request:
  push:
    branches: [main]

jobs:
  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json
      - name: Install dependencies
        run: cd frontend && npm ci
      - name: Lint
        run: cd frontend && npm run lint
      - name: Test
        run: cd frontend && npm run test:ci
      - name: Build
        run: cd frontend && npm run build -- --configuration production

  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Setup Gradle
        uses: gradle/gradle-build-action@v2
      - name: Run tests
        run: cd backend && ./gradlew test
      - name: Run integration tests
        run: cd backend && ./gradlew integrationTest
      - name: Build JAR
        run: cd backend && ./gradlew bootJar
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

  docker-build:
    runs-on: ubuntu-latest
    needs: [frontend-test, backend-test]
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      - name: Build frontend image
        run: docker build -f Dockerfile.frontend -t smartbudget-frontend:latest .
      - name: Build backend image
        run: docker build -f Dockerfile.backend -t smartbudget-backend:latest .
```

## Environment Configuration

**Frontend (environment.prod.ts):**
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.smartbudgetapp.com/api',
  version: '1.0.0'
};
```

**Backend (application-prod.yml):**
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000 # 24 hours

logging:
  level:
    root: INFO
    com.smartbudget: INFO
  pattern:
    console: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"

server:
  port: 8080
  compression:
    enabled: true
```
