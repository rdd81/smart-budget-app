# Smart Budget App

[![CI Pipeline](https://github.com/YOUR_USERNAME/smart-budget-app/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/smart-budget-app/actions/workflows/ci.yml)

A modern personal finance management application that helps users track income and expenses, visualize spending patterns, and make informed financial decisions.

## Technology Stack

### Frontend
- **Angular 20.3.0** - Progressive web framework
- **TypeScript 5.9.2** - Strongly typed JavaScript superset
- **SCSS** - Enhanced CSS with variables and mixins
- **RxJS 7.8.0** - Reactive programming library
- **Angular Router** - Client-side routing
- **Jasmine + Karma** - Unit testing framework

### Backend
- **Java 21 LTS** - Long-term support Java runtime
- **Spring Boot 3.4.1** - Enterprise-grade application framework
- **Spring Web** - RESTful API development
- **Spring Data JPA** - Database abstraction layer
- **Spring Security** - Authentication and authorization
- **PostgreSQL Driver** - Database connectivity
- **Gradle 8.14.3** - Build automation tool
- **JUnit 5 + Mockito** - Testing framework

### Infrastructure
- **PostgreSQL 15** - Relational database (Alpine Docker image)
- **Docker 24+** - Containerization with multi-stage builds
- **Docker Compose** - Service orchestration
- **NGINX** - Web server and reverse proxy
- **GitHub Actions** - CI/CD pipeline (to be configured in Story 1.4)

## Prerequisites

Before running this application, ensure you have the following installed:

- **Node.js 20 LTS** or higher - [Download](https://nodejs.org/)
- **npm 10+** - Comes with Node.js
- **Java 21 LTS** - [Download OpenJDK](https://adoptium.net/) or [Amazon Corretto](https://aws.amazon.com/corretto/)
- **Docker 24+** - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/) (for future containerization)
- **Git 2.40+** - [Download](https://git-scm.com/)

### Verify Prerequisites

```bash
# Check Node.js version
node --version  # Should be v20.x.x or higher

# Check npm version
npm --version  # Should be 10.x.x or higher

# Check Java version
java -version  # Should be 21.x.x

# Check Docker version
docker --version  # Should be 24.x.x or higher

# Check Git version
git --version  # Should be 2.40.x or higher
```

## Project Structure

```
smart-budget-app/
├── frontend/              # Angular 20 application
│   ├── src/
│   │   ├── app/          # Application components and services
│   │   ├── assets/       # Static assets (images, fonts)
│   │   └── environments/ # Environment-specific configurations
│   ├── package.json      # npm dependencies
│   ├── tsconfig.json     # TypeScript configuration (strict mode)
│   ├── angular.json      # Angular CLI configuration
│   ├── Dockerfile        # Production Docker image (multi-stage build)
│   └── Dockerfile.dev    # Development Docker image (hot-reload)
│
├── backend/               # Spring Boot 3.4 application
│   ├── src/
│   │   ├── main/java/    # Java source code
│   │   │   └── com/smartbudget/
│   │   ├── main/resources/ # Application properties
│   │   └── test/java/    # Test source code
│   ├── build.gradle      # Gradle build configuration
│   ├── gradlew           # Gradle wrapper (Unix/Mac)
│   ├── gradlew.bat       # Gradle wrapper (Windows)
│   └── Dockerfile        # Production Docker image (multi-stage build)
│
├── docs/                  # Project documentation
│   ├── prd/              # Product requirements documents
│   ├── architecture/     # Architecture specifications
│   └── sprint-artifacts/ # User stories and tech specs
│
├── docker-compose.yml     # Docker orchestration (production mode)
├── docker-compose.dev.yml # Docker orchestration (development mode)
├── nginx.conf            # NGINX configuration for frontend
├── .gitignore            # Git ignore patterns
└── README.md             # This file
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd smart-budget-app
```

### 2. Run the Frontend (Angular)

```bash
cd frontend
npm install
npm start
```

The Angular dev server will start on **http://localhost:4200**

### 3. Run the Backend (Spring Boot)

Open a new terminal window:

```bash
cd backend
./gradlew bootRun   # On Unix/Mac/Linux
gradlew.bat bootRun # On Windows
```

The Spring Boot application will start on **http://localhost:8080**

### 4. Access the Application

- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080

## Running with Docker

The application is fully containerized with Docker, allowing you to run the entire stack (frontend, backend, and database) with a single command.

### Prerequisites

- **Docker 24+** - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Docker Compose** - Included with Docker Desktop

### Production Mode

Run the complete application stack in production mode:

```bash
# Start all services (frontend, backend, database)
docker-compose up

# Start in detached mode (background)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove all volumes (clean restart)
docker-compose down -v
```

**Access the application:**
- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8080
- **PostgreSQL:** localhost:5432 (database: smartbudget, user: postgres, password: postgres)

### Development Mode (Hot-Reload)

Run the application with hot-reload support for rapid development:

```bash
# Start in development mode with hot-reload
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up

# Start in detached mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

**Development mode features:**
- Frontend hot-reload: Changes to `frontend/src` are automatically reflected
- Backend hot-reload: Spring DevTools enabled for automatic restarts
- Source code mounted as volumes for live updates

### Docker Services

The application stack consists of three services:

1. **postgres** - PostgreSQL 15 Alpine database
   - Port: 5432
   - Data persistence via Docker volume `postgres_data`
   - Health checks enabled

2. **backend** - Spring Boot 3.4.1 application
   - Port: 8080
   - Multi-stage build (Gradle build → JRE runtime)
   - Depends on postgres service
   - Health checks via Spring Actuator

3. **frontend** - Angular 20.3.0 application
   - Port: 4200 (mapped from container port 80)
   - Multi-stage build (npm build → NGINX serving)
   - Proxies /api requests to backend
   - NGINX with gzip compression

### Common Docker Commands

```bash
# Rebuild images (after Dockerfile changes)
docker-compose build

# Rebuild without using cache
docker-compose build --no-cache

# View running containers
docker-compose ps

# View logs for specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# Execute command in running container
docker-compose exec backend sh
docker-compose exec frontend sh
docker-compose exec postgres psql -U postgres -d smartbudget

# Stop specific service
docker-compose stop backend

# Restart specific service
docker-compose restart backend

# Remove all containers and networks
docker-compose down

# Remove all containers, networks, and volumes
docker-compose down -v
```

### Troubleshooting Docker

**Port conflicts:**
```bash
# Check what's using a port (Windows)
netstat -ano | findstr :8080
taskkill /F /PID <pid>

# Check what's using a port (Unix/Mac/Linux)
lsof -i :8080
kill -9 <pid>
```

**Database connection issues:**
- Ensure postgres service is healthy: `docker-compose ps`
- Check backend logs: `docker-compose logs backend`
- Verify database credentials in docker-compose.yml

**Build failures:**
- Clean build: `docker-compose build --no-cache`
- Remove old images: `docker image prune -a`
- Check Docker disk space: `docker system df`

## Running Tests

### Frontend Tests

```bash
cd frontend
npm test        # Run tests in watch mode
npm run test:ci # Run tests once (for CI/CD)
```

**Test Coverage Target:** 60% minimum line coverage

### Backend Tests

```bash
cd backend
./gradlew test        # Run all tests
./gradlew test --info # Run tests with detailed output
```

**Test Coverage Target:** 70% minimum line coverage

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration and deployment. Every push to `main` and all pull requests are automatically tested and validated.

### Workflow Jobs

1. **Frontend Tests** - Runs linting, unit tests, and generates coverage reports
2. **Backend Tests** - Runs Gradle build, unit tests, integration tests with TestContainers, and generates coverage reports
3. **Docker Build** - Validates that all Docker images build successfully
4. **Build Artifacts** - Generates production-ready JAR and frontend dist files

### Coverage Requirements

- **Frontend:** Minimum 60% coverage (statements, branches, functions, lines)
- **Backend:** Minimum 70% instruction coverage

The build will fail if coverage thresholds are not met or if any tests fail.

### Running Coverage Locally

**Frontend:**
```bash
cd frontend
npm run test -- --code-coverage --no-watch
```

**Backend:**
```bash
cd backend
./gradlew test jacocoTestReport
./gradlew jacocoTestCoverageVerification
```

Coverage reports:
- Frontend: `frontend/coverage/index.html`
- Backend: `backend/build/reports/jacoco/test/html/index.html`

## Build for Production

### Frontend Production Build

```bash
cd frontend
npm run build
```

Build artifacts will be stored in `frontend/dist/`

### Backend Production Build

```bash
cd backend
./gradlew build
```

JAR file will be created in `backend/build/libs/`

## Development Workflow

### Code Quality

- **Frontend:** ESLint and Prettier (configured in Angular CLI)
- **Backend:** Follow Spring Boot best practices

### Testing Strategy

- **Unit Tests:** Test business logic in isolation
- **Integration Tests:** Test component interactions
- **E2E Tests:** Test critical user flows (to be configured)

### Architecture Patterns

- **Frontend:** Component-based architecture with Angular standalone components
- **Backend:** Layered architecture (Controller → Service → Repository)

## Project Status

**Current Sprint:** Epic 1 - Foundation & Core Infrastructure

**Completed Stories:**
- ✅ Story 1.1: Project Initialization and Repository Structure
- ✅ Story 1.2: Docker Containerization and Local Development Environment

**Upcoming Stories:**
- Story 1.3: Database Schema and Migration Framework
- Story 1.4: CI/CD Pipeline Setup
- Story 1.5-1.9: User Authentication and Profile Management

## Documentation

For detailed documentation, see the `docs/` directory:

- **Product Requirements:** [docs/prd/](docs/prd/)
- **Architecture:** [docs/architecture.md](docs/architecture.md)
- **Sprint Artifacts:** [docs/sprint-artifacts/](docs/sprint-artifacts/)
- **Tech Specifications:** [docs/sprint-artifacts/tech-spec-epic-1.md](docs/sprint-artifacts/tech-spec-epic-1.md)

## Contributing

This project follows the BMAD (BMad Method) development methodology for AI-assisted software development.

## License

[To be determined]

## Support

For questions or issues, please refer to the project documentation in the `docs/` directory.
