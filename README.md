# Smart Budget App

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
- **PostgreSQL 15+** - Relational database (to be configured in Story 1.3)
- **Docker 24+** - Containerization (to be configured in Story 1.2)
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
│   └── angular.json      # Angular CLI configuration
│
├── backend/               # Spring Boot 3.4 application
│   ├── src/
│   │   ├── main/java/    # Java source code
│   │   │   └── com/smartbudget/
│   │   ├── main/resources/ # Application properties
│   │   └── test/java/    # Test source code
│   ├── build.gradle      # Gradle build configuration
│   ├── gradlew           # Gradle wrapper (Unix/Mac)
│   └── gradlew.bat       # Gradle wrapper (Windows)
│
├── docs/                  # Project documentation
│   ├── prd/              # Product requirements documents
│   ├── architecture/     # Architecture specifications
│   └── sprint-artifacts/ # User stories and tech specs
│
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

**Upcoming Stories:**
- Story 1.2: Docker Containerization and Local Development Environment
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
