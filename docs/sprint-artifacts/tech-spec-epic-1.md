# Epic Technical Specification: Foundation & Core Infrastructure

Date: 2025-11-15
Author: Radka
Epic ID: 1
Status: Draft

---

## Overview

Epic 1 establishes the complete technical foundation for the Smart Budget App, a full-stack personal finance management application. This epic delivers the core infrastructure, development tooling, and user authentication system that enables all subsequent business features. The scope encompasses project structure setup, containerization with Docker, database schema implementation using PostgreSQL with Flyway migrations, CI/CD pipeline automation via GitHub Actions, and a complete JWT-based authentication system with registration, login, and profile management capabilities. Upon completion, the application will be fully deployable with working user authentication, providing a production-ready platform for transaction management, analytics, and AI categorization features in subsequent epics.

## Objectives and Scope

**In Scope:**
- Monorepo project initialization with Angular 17+ frontend and Spring Boot 3.2+ backend
- Docker and docker-compose configuration for local development and production deployment
- PostgreSQL 15+ database schema with Flyway version-controlled migrations
- Complete user authentication system (registration, login, JWT tokens, profile management)
- CI/CD pipeline with automated testing, code coverage enforcement, and Docker image builds
- Security implementation: BCrypt password hashing, JWT signing, HTTPS enforcement, CORS configuration
- Deployment documentation and environment configuration strategy
- Health check endpoints and basic observability setup

**Out of Scope:**
- Business features (transaction management, dashboard, analytics) - covered in Epics 2-4
- Email verification workflow - post-MVP enhancement
- Advanced monitoring and alerting - basic logging only for MVP
- Multi-factor authentication - future security enhancement
- Social login (OAuth) - future authentication enhancement

## System Architecture Alignment

Epic 1 implements the foundational architectural components defined in the system architecture:

**Layered Architecture (Spring Boot Backend):**
- Controller layer: REST API endpoints for authentication and user profile
- Service layer: Business logic for user registration, authentication, and password management
- Repository layer: JPA repositories for data access with Spring Data JPA
- Security layer: Spring Security with JWT filter for stateless authentication

**Component-Based Frontend (Angular):**
- Core module: Singleton services (AuthService, HTTP interceptors, route guards)
- Shared module: Reusable components and utilities
- Feature modules: Auth module with lazy loading support
- State management: LocalStorage for JWT tokens, reactive forms for user input

**Data Layer:**
- PostgreSQL database with User entity schema
- Flyway migrations for version-controlled schema evolution
- Connection pooling and transaction management via Spring Data JPA

**Infrastructure:**
- Docker containers for frontend (NGINX), backend (Java), and database (PostgreSQL)
- GitHub Actions for automated CI/CD with test execution and coverage validation
- Multi-stage Docker builds for optimized production images

**Security Architecture:**
- Stateless JWT authentication with 24-hour token expiration
- BCrypt password hashing (work factor 10)
- HTTPS/TLS 1.2+ enforcement in production
- CORS whitelisting for frontend origin
- Input validation at all layers (Bean Validation, Angular reactive forms)

## Detailed Design

### Services and Modules

| Module/Service | Responsibility | Key Inputs | Key Outputs | Owner |
|----------------|----------------|------------|-------------|-------|
| **Backend: AuthController** | Handle authentication HTTP requests | RegisterRequest, LoginRequest DTOs | AuthResponse (JWT token + user) | Backend |
| **Backend: AuthService** | Business logic for user authentication | Email, password, User entity | JWT token, User entity | Backend |
| **Backend: UserController** | Handle user profile HTTP requests | UpdateEmailRequest, UpdatePasswordRequest | UserProfile DTO | Backend |
| **Backend: UserService** | Business logic for profile management | Email, password, userId | User entity, validation results | Backend |
| **Backend: JwtService** | Generate and validate JWT tokens | User claims (id, email) | JWT token string, validation result | Backend |
| **Backend: UserRepository** | Data access for User entity | User queries (findByEmail, save) | User entity from database | Backend |
| **Backend: SecurityConfig** | Spring Security configuration | JWT filter, CORS settings | Security filter chain | Backend |
| **Frontend: AuthService (Angular)** | Client-side auth state management | User credentials, JWT token | Observable<User>, auth state | Frontend |
| **Frontend: AuthGuard** | Route protection | Current auth state | boolean (allow/deny navigation) | Frontend |
| **Frontend: JwtInterceptor** | Attach JWT to HTTP requests | Outgoing HTTP requests | Modified requests with Auth header | Frontend |
| **Frontend: LoginComponent** | User login UI | User input (email, password) | Navigation to dashboard on success | Frontend |
| **Frontend: RegisterComponent** | User registration UI | User input (email, password, confirm) | Navigation to dashboard on success | Frontend |
| **Frontend: ProfileComponent** | User profile management UI | User input (new email, passwords) | Updated profile, success/error messages | Frontend |

### Data Models and Contracts

**User Entity (Backend - JPA)**

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;  // BCrypt hashed

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Getters, setters, constructors
}
```

**User Interface (Frontend - TypeScript)**

```typescript
export interface User {
  id: string;
  email: string;
  createdAt: Date;
  updatedAt: Date;
}
```

**Request DTOs (Backend)**

```java
// Registration
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;
}

// Login
public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

// Update Email
public class UpdateEmailRequest {
    @Email
    @NotBlank
    private String newEmail;
}

// Update Password
public class UpdatePasswordRequest {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
```

**Response DTOs (Backend)**

```java
public class AuthResponse {
    private String accessToken;
    private UserResponse user;
}

public class UserResponse {
    private UUID id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // passwordHash intentionally excluded
}
```

**Database Schema (Flyway Migration V1__initial_schema.sql)**

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### APIs and Interfaces

**Authentication Endpoints**

| Method | Endpoint | Request Body | Response | Auth Required | Description |
|--------|----------|--------------|----------|---------------|-------------|
| POST | `/api/auth/register` | RegisterRequest | 201 Created: AuthResponse | No | Register new user account |
| POST | `/api/auth/login` | LoginRequest | 200 OK: AuthResponse | No | Login and receive JWT token |

**User Profile Endpoints**

| Method | Endpoint | Request Body | Response | Auth Required | Description |
|--------|----------|--------------|----------|---------------|-------------|
| GET | `/api/users/profile` | - | 200 OK: UserResponse | Yes (JWT) | Get authenticated user's profile |
| PUT | `/api/users/profile/email` | UpdateEmailRequest | 200 OK: UserResponse | Yes (JWT) | Update user email |
| PUT | `/api/users/profile/password` | UpdatePasswordRequest | 200 OK: Success message | Yes (JWT) | Change user password |

**Health Check Endpoint**

| Method | Endpoint | Request Body | Response | Auth Required | Description |
|--------|----------|--------------|----------|---------------|-------------|
| GET | `/api/health` | - | 200 OK: `{"status": "UP"}` | No | Application health status |

**Error Response Format**

```json
{
  "timestamp": "2025-11-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

**JWT Token Structure**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-uuid",
    "email": "user@example.com",
    "iat": 1699564800,
    "exp": 1699651200
  }
}
```

**HTTP Status Codes**

- `200 OK` - Successful request
- `201 Created` - Resource created successfully (registration)
- `400 Bad Request` - Validation error, duplicate email
- `401 Unauthorized` - Invalid credentials, missing/invalid token
- `403 Forbidden` - Valid token but insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Workflows and Sequencing

**User Registration Flow**

```
1. User submits registration form (email, password, confirm password)
2. Frontend validates:
   - Email format
   - Password minimum 8 characters
   - Password matches confirmation
3. Frontend POSTs to /api/auth/register
4. Backend validates request DTO
5. Backend checks email uniqueness in database
6. If duplicate: Return 400 "Email already exists"
7. Backend hashes password with BCrypt (work factor 10)
8. Backend creates User entity and persists to database
9. Backend generates JWT token (24h expiration)
10. Backend returns 201 with AuthResponse {token, user}
11. Frontend stores token in localStorage
12. Frontend navigates to /dashboard
```

**User Login Flow**

```
1. User submits login form (email, password)
2. Frontend validates required fields
3. Frontend POSTs to /api/auth/login
4. Backend queries database for user by email
5. If not found: Return 401 "Invalid credentials"
6. Backend verifies password with BCrypt.matches()
7. If mismatch: Return 401 "Invalid credentials"
8. Backend generates JWT token (24h expiration)
9. Backend returns 200 with AuthResponse {token, user}
10. Frontend stores token in localStorage
11. Frontend navigates to /dashboard
```

**Authenticated API Request Flow**

```
1. User performs action requiring authentication
2. Angular service makes HTTP request
3. JwtInterceptor reads token from localStorage
4. JwtInterceptor adds Authorization header: "Bearer <token>"
5. Request sent to backend
6. Spring Security JwtAuthenticationFilter intercepts request
7. Filter extracts and validates JWT token
8. If invalid/expired: Return 401 Unauthorized
9. If valid: Extract userId from token claims
10. Set SecurityContext with authenticated user
11. Controller executes business logic
12. Service verifies user ownership (e.g., userId matches resource)
13. Response returned to frontend
```

**Profile Email Update Flow**

```
1. User enters new email in ProfileComponent
2. Frontend validates email format
3. Frontend PUTs to /api/users/profile/email
4. Backend validates JWT and extracts userId
5. Backend validates new email format
6. Backend checks email uniqueness
7. If duplicate: Return 400 "Email already exists"
8. Backend updates user.email and user.updatedAt
9. Backend persists to database
10. Backend returns 200 with updated UserResponse
11. Frontend displays success notification
12. Frontend refreshes displayed profile data
```

**Profile Password Update Flow**

```
1. User enters current password, new password, confirm new password
2. Frontend validates:
   - All fields required
   - New password minimum 8 characters
   - New password matches confirmation
3. Frontend PUTs to /api/users/profile/password
4. Backend validates JWT and extracts userId
5. Backend retrieves user from database
6. Backend verifies currentPassword with BCrypt.matches()
7. If mismatch: Return 401 "Current password is incorrect"
8. Backend validates new password length
9. Backend hashes new password with BCrypt
10. Backend updates user.passwordHash and user.updatedAt
11. Backend persists to database
12. Backend returns 200 with success message
13. Frontend displays success notification
```

**Logout Flow**

```
1. User clicks logout button
2. Frontend removes JWT token from localStorage
3. Frontend clears any in-memory auth state
4. Frontend navigates to /login
5. Token expires naturally after 24 hours (no server-side blacklist)
```

## Non-Functional Requirements

### Performance

**Targets (from PRD NFR1-NFR4):**

| Metric | Target | Scope |
|--------|--------|-------|
| Page Load Time | < 2 seconds | All pages on standard broadband |
| User Interaction Response | < 200ms | Button clicks, form submissions |
| API Response Time | < 500ms | Authentication endpoints (p95) |
| Database Query Time | < 100ms | User lookup by email (p95) |
| Docker Startup Time | < 30 seconds | Full stack (all 3 containers) |

**Performance Optimizations:**

- **Frontend:**
  - Lazy loading for feature modules (not applicable in Epic 1, only auth module)
  - Angular production build with AOT compilation and tree shaking
  - NGINX static file serving with gzip compression
  - Minified and bundled JavaScript/CSS

- **Backend:**
  - Connection pooling (HikariCP) with default settings (10 connections)
  - Database index on `users.email` for fast lookups
  - Spring Boot embedded Tomcat with default thread pool (200 threads)
  - JWT token validation without database query (stateless)

- **Database:**
  - PostgreSQL query optimization with EXPLAIN ANALYZE for user queries
  - Appropriate indexing strategy (email unique index)
  - No N+1 queries (single query for user lookup)

**Measurement Strategy:**
- JMeter load testing for API endpoints (100 concurrent users)
- Lighthouse performance audits for frontend pages (target score: 90+)
- Spring Boot Actuator metrics for API response times
- Database query logging in development (slow query log threshold: 100ms)

### Security

**Authentication & Authorization (from Architecture Security section):**

- **Password Security:**
  - BCrypt hashing with work factor 10 (NFR7)
  - Minimum password length: 8 characters
  - No password complexity requirements for MVP (user choice)
  - Password never returned in API responses or logs

- **JWT Token Security:**
  - HMAC-SHA256 signing algorithm
  - Secret key stored in environment variable `JWT_SECRET` (minimum 256 bits)
  - Token expiration: 24 hours from issuance
  - Token includes: user ID, email, issued-at, expiration
  - No sensitive data in token payload (publicly readable)
  - Stateless validation (no database lookup required)

- **Transport Security (NFR8):**
  - HTTPS enforced in production (TLS 1.2 or higher)
  - HTTP Strict Transport Security (HSTS) header enabled
  - Secure cookie flags (if cookies used in future)
  - Development: HTTP allowed for localhost only

- **CORS Configuration:**
  - Whitelist frontend origin only (no wildcard `*`)
  - Development: `http://localhost:4200`
  - Production: Configured via environment variable `ALLOWED_ORIGINS`
  - Preflight requests handled correctly

- **Input Validation:**
  - Backend: Bean Validation annotations (@Email, @NotBlank, @Size)
  - Frontend: Angular reactive form validators
  - SQL injection prevention: Parameterized queries via JPA
  - XSS prevention: Angular's built-in sanitization

- **Data Protection (NFR9):**
  - User passwords encrypted at rest via BCrypt hashing
  - Database credentials in environment variables
  - No secrets in source code or Git repository
  - Application logs do not contain passwords or tokens

- **Security Headers:**
  - Content-Security-Policy: Restrict script sources
  - X-Frame-Options: DENY (prevent clickjacking)
  - X-Content-Type-Options: nosniff
  - X-XSS-Protection: 1; mode=block

**Authorization Rules:**
- All `/api/users/profile/*` endpoints require valid JWT token
- Users can only access/modify their own profile (userId from token)
- No role-based access control in Epic 1 (all users have same permissions)

### Reliability/Availability

**Availability Targets:**
- Development: Best-effort (local development)
- Staging: 95% uptime (acceptable downtime for testing)
- Production: 99% uptime target (MVP goal)

**Error Handling:**

- **Backend Error Handling:**
  - Global exception handler (@ControllerAdvice)
  - Structured error responses with timestamp, status, message, path
  - Graceful degradation for database connection failures
  - Retry logic not required for Epic 1 (simple operations)

- **Frontend Error Handling:**
  - HTTP error interceptor catches 401/403/500 responses
  - User-friendly error messages displayed (no stack traces)
  - Automatic logout and redirect on 401 Unauthorized
  - Network error detection with retry prompt

**Data Consistency:**
- Database transactions for user creation (atomic operation)
- Email uniqueness enforced at database level (UNIQUE constraint)
- Optimistic locking not required (no concurrent updates expected)
- Password update requires current password verification

**Recovery:**
- Database data persisted in Docker volume (survives container restart)
- Application restarts cleanly without manual intervention
- No state stored in application memory (stateless design)
- JWT tokens remain valid across application restarts

### Observability

**Logging (NFR18):**

- **Backend Logging:**
  - Framework: SLF4J with Logback
  - Log levels: ERROR, WARN, INFO, DEBUG, TRACE
  - Development: DEBUG level to console
  - Production: INFO level to file (logs/application.log)
  - Structured logging: JSON format for production (optional)

- **Log Content:**
  - INFO: User registration, login attempts (email only, no passwords)
  - WARN: Invalid credentials, duplicate email attempts
  - ERROR: Database connection failures, unexpected exceptions
  - DEBUG: JWT token generation (not token content), validation results

- **Security Logging:**
  - Failed login attempts logged with email (rate limiting insight)
  - Password change events logged with user ID
  - No sensitive data in logs (passwords, full tokens)

**Metrics:**

- **Spring Boot Actuator Endpoints:**
  - `/actuator/health` - Application health status
  - `/actuator/metrics` - JVM, HTTP, database metrics (secured for production)
  - `/actuator/info` - Build information, version

- **Key Metrics to Track:**
  - Authentication success/failure rates
  - API request counts and response times
  - Database connection pool usage
  - JVM memory and CPU utilization

**Monitoring Strategy:**
- Development: Console logs and Spring Boot Actuator
- Staging: File-based logs with manual review
- Production: Structured logs ready for aggregation (ELK stack future enhancement)
- No real-time alerting for MVP (manual monitoring)

**Debugging Support:**
- Correlation IDs for request tracing (future enhancement)
- Thread names in log output
- Exception stack traces logged at ERROR level
- Spring Boot DevTools in development for hot reload

## Dependencies and Integrations

### Frontend Dependencies (Angular)

**Core Framework:**
- **Angular**: 17+ (latest stable)
  - Angular CLI for project scaffolding and build tooling
  - Angular Router for client-side routing
  - Angular Forms (Reactive Forms) for user input handling
  - Angular Common HTTP for API communication
- **TypeScript**: 5.2+ (strict mode enabled)
  - Compile-time type safety
  - Enhanced IDE support and refactoring

**UI Component Library:**
- **Angular Material**: 17+ (matching Angular version)
  - Pre-built UI components (mat-form-field, mat-button, mat-card, mat-toolbar, mat-snackbar)
  - Accessibility (ARIA) support built-in
  - Responsive design patterns
- **Tailwind CSS**: 3.4+
  - Utility-first CSS framework
  - Custom theme configuration for brand colors
  - JIT compilation for optimal bundle size

**State Management and Utilities:**
- **RxJS**: 7.8+ (included with Angular)
  - Observable-based reactive programming
  - HTTP request handling and error management
- **date-fns**: 3.0+ (optional for date formatting if needed)

**Testing:**
- **Jasmine**: 5.1+ (behavior-driven testing framework)
- **Karma**: 6.4+ (test runner for Angular)
- **@angular/core/testing**: Testing utilities for Angular components and services

**Build Tools:**
- **Node.js**: 20 LTS (required for npm and Angular CLI)
- **npm**: 10+ (package manager)
- **Angular CLI**: 17+ (build, serve, test, lint commands)
- **Webpack**: 5+ (bundled with Angular CLI, no direct configuration needed)

### Backend Dependencies (Spring Boot)

**Core Framework:**
- **Java**: 21 LTS (Amazon Corretto or OpenJDK)
- **Spring Boot**: 3.2+ (latest stable)
  - Spring Boot Starter Web (REST API)
  - Spring Boot Starter Data JPA (data access)
  - Spring Boot Starter Security (authentication/authorization)
  - Spring Boot Starter Validation (Bean Validation)
  - Spring Boot Actuator (health checks and metrics)
- **Spring Framework**: 6.1+ (included with Spring Boot)
- **Spring Security**: 6.2+ (JWT authentication, CORS, security headers)

**Data Access:**
- **Spring Data JPA**: 3.2+ (repository abstraction)
- **Hibernate ORM**: 6.4+ (JPA implementation)
- **PostgreSQL JDBC Driver**: 42.7+ (database connectivity)
- **HikariCP**: 5.1+ (connection pooling, included with Spring Boot)

**Security:**
- **BCrypt**: Included in Spring Security (password hashing)
- **jjwt (Java JWT)**: 0.12.3+ (JWT token generation and validation)
  - io.jsonwebtoken:jjwt-api
  - io.jsonwebtoken:jjwt-impl
  - io.jsonwebtoken:jjwt-jackson

**Database Migration:**
- **Flyway**: 10.4+ (version-controlled schema migrations)
  - Flyway Core for migration execution
  - Integration with Spring Boot auto-configuration

**Testing:**
- **JUnit 5 (Jupiter)**: 5.10+ (unit testing framework)
- **Mockito**: 5.8+ (mocking framework for unit tests)
- **Spring Boot Test**: Testing support for Spring components
- **TestContainers**: 1.19+ (integration testing with PostgreSQL container)
  - TestContainers PostgreSQL module
  - Docker required for TestContainers execution

**Build Tool:**
- **Gradle**: 8.5+ (build automation)
  - Gradle Wrapper (gradlew) for version consistency
  - Spring Boot Gradle Plugin for packaging
  - Dependency management plugin

**Logging:**
- **SLF4J**: 2.0+ (logging facade, included with Spring Boot)
- **Logback**: 1.4+ (logging implementation, default in Spring Boot)

### Infrastructure Dependencies

**Containerization:**
- **Docker**: 24+ (container runtime)
  - Multi-stage builds for optimized production images
  - Docker Compose for local development orchestration
- **Docker Compose**: 2.23+ (multi-container orchestration)
  - PostgreSQL database service
  - Spring Boot backend service
  - NGINX frontend service

**Database:**
- **PostgreSQL**: 15+ (Docker image: postgres:15-alpine)
  - Alpine Linux base for smaller image size
  - Persistent volume for data storage
  - Exposed port 5432 for local development

**Web Server (Frontend):**
- **NGINX**: 1.25+ (Docker image: nginx:1.25-alpine)
  - Serve Angular production build static files
  - Reverse proxy configuration for API requests
  - Gzip compression enabled

**CI/CD:**
- **GitHub Actions**: Latest (CI/CD automation)
  - Workflow runners: ubuntu-latest
  - Actions used: actions/checkout@v4, actions/setup-java@v4, actions/setup-node@v4
  - Docker buildx for multi-platform image builds (if needed)

### External Integration Points

**Development Environment:**
- **No external services required for MVP** - All dependencies run locally or in CI/CD

**Future Integration Points (Post-MVP):**
- Email service (e.g., SendGrid, AWS SES) for email verification
- Cloud hosting (e.g., AWS, Azure, GCP) for production deployment
- Logging aggregation (e.g., ELK stack, CloudWatch)
- Monitoring and alerting (e.g., Prometheus, Grafana, Datadog)

### Internal Integration Points

**Frontend ↔ Backend Integration:**
- **Protocol**: HTTP/HTTPS REST API
- **Data Format**: JSON (application/json)
- **Authentication**: JWT Bearer token in Authorization header
- **CORS**: Frontend origin whitelisted in Spring Security configuration
- **Error Handling**: Structured error responses with HTTP status codes
- **API Base URL**:
  - Development: `http://localhost:8080/api`
  - Production: Configured via environment variable `API_BASE_URL`

**Backend ↔ Database Integration:**
- **Protocol**: JDBC over TCP/IP (PostgreSQL wire protocol)
- **Connection String**: `jdbc:postgresql://localhost:5432/smart_budget_db`
  - Host, port, database name configurable via environment variables
- **Connection Pooling**: HikariCP with default settings (10 max connections)
- **Transaction Management**: Spring's @Transactional annotation with JPA
- **Migration Execution**: Flyway runs on application startup (development and production)

**Docker Container Networking:**
- **Docker Compose Network**: Custom bridge network `smart-budget-network`
- **Service Discovery**: Services reference each other by service name
  - Frontend (NGINX) → Backend: `http://backend:8080`
  - Backend → Database: `postgresql://postgres:5432`
- **Port Mapping**:
  - Frontend: 4200:80 (development), 80:80 (production)
  - Backend: 8080:8080
  - Database: 5432:5432 (exposed for local development tools)

### Dependency Version Management

**Frontend (package.json):**
- Exact versions specified for Angular core packages to ensure compatibility
- Caret (^) versioning for non-critical dependencies (allows minor/patch updates)
- Lock file (package-lock.json) committed to ensure reproducible builds

**Backend (build.gradle):**
- Spring Boot Dependency Management plugin handles transitive dependency versions
- Explicit version constraints for non-Spring dependencies (jjwt, TestContainers)
- Gradle lock file (gradle.lockfile) for reproducible builds

**Infrastructure (docker-compose.yml):**
- Specific major.minor versions for PostgreSQL and NGINX images
- Alpine variants for smaller image sizes
- Image digests (SHA256) not used for MVP (flexibility for minor updates)

### Security Considerations for Dependencies

**Dependency Scanning:**
- **npm audit**: Run during CI/CD to detect frontend vulnerabilities
- **Gradle dependency-check plugin**: Scan backend dependencies for CVEs
- **Dependabot**: GitHub Dependabot enabled for automated security updates

**Trusted Sources:**
- Frontend: npm registry (registry.npmjs.org)
- Backend: Maven Central (repo.maven.apache.org)
- Infrastructure: Docker Hub official images (verified publishers)

**Update Strategy:**
- Security patches applied within 7 days of disclosure
- Minor version updates reviewed monthly
- Major version updates planned for post-MVP unless critical security fix

## Acceptance Criteria (Authoritative)

This section consolidates all 107 acceptance criteria from Epic 1's 9 stories, organized sequentially for implementation tracking and test coverage validation.

### Story 1.1: Project Initialization and Repository Structure (9 ACs)

**AC 1.1-1:** Monorepo directory structure created with `frontend/` and `backend/` subdirectories

**AC 1.1-2:** Angular 17+ application generated using Angular CLI in `frontend/` directory with TypeScript strict mode enabled

**AC 1.1-3:** Spring Boot 3.x application generated (using Spring Initializr or Gradle init) in `backend/` directory with Java 21, Spring Web, Spring Data JPA, Spring Security, and PostgreSQL dependencies

**AC 1.1-4:** Root-level README.md created with project overview, technology stack, and basic getting started instructions

**AC 1.1-5:** .gitignore configured for both Node.js and Java/Gradle artifacts

**AC 1.1-6:** Both frontend and backend applications can run independently (Angular dev server on port 4200, Spring Boot on port 8080)

**AC 1.1-7:** Gradle wrapper included in backend/ for consistent build experience

**AC 1.1-8:** npm/pnpm package.json includes Angular 17+ and required dependencies

**AC 1.1-9:** Basic folder structure follows BMAD standards with docs/ directory created

### Story 1.2: Docker Containerization and Local Development Environment (10 ACs)

**AC 1.2-1:** Dockerfile created for Angular frontend (multi-stage build: npm build → nginx serving static files)

**AC 1.2-2:** Dockerfile created for Spring Boot backend (multi-stage build: Gradle build → Java runtime)

**AC 1.2-3:** docker-compose.yml orchestrates three services: frontend, backend, and PostgreSQL database

**AC 1.2-4:** PostgreSQL service configured with volume mounting for data persistence and initialization scripts

**AC 1.2-5:** Environment variables properly configured for database connection (host, port, username, password)

**AC 1.2-6:** Frontend container proxies API requests to backend container (NGINX configuration or Angular proxy.conf.json)

**AC 1.2-7:** Running `docker-compose up` successfully starts all services and application is accessible at http://localhost

**AC 1.2-8:** Development mode supports hot-reload for both frontend (volume mount) and backend (optional: Spring DevTools)

**AC 1.2-9:** docker-compose.yml includes appropriate health checks for all services

**AC 1.2-10:** README.md updated with Docker setup instructions and common commands

### Story 1.3: Database Schema and Migration Framework (10 ACs)

**AC 1.3-1:** Flyway configured as migration tool with dependencies added to build.gradle

**AC 1.3-2:** Initial migration script created (V1__initial_schema.sql) defining users, transactions, and categories tables

**AC 1.3-3:** Users table includes: id (UUID), email (unique), password_hash, created_at, updated_at

**AC 1.3-4:** Categories table includes: id, name, type (income/expense), description with seed data for predefined categories (Salary, Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Savings, Investments, Other)

**AC 1.3-5:** Transactions table includes: id (UUID), user_id (FK to users), amount (decimal), transaction_date, description, category_id (FK to categories), transaction_type (income/expense enum), created_at, updated_at

**AC 1.3-6:** Appropriate indexes created on foreign keys and frequently queried columns (user_id, transaction_date, category_id)

**AC 1.3-7:** Database migrations run automatically on application startup

**AC 1.3-8:** JPA entity classes created in backend matching database schema with proper annotations (@Entity, @Table, @Column, relationships)

**AC 1.3-9:** Repository interfaces created extending JpaRepository for User, Transaction, and Category entities

**AC 1.3-10:** Application successfully connects to PostgreSQL and migrations execute without errors

### Story 1.4: CI/CD Pipeline Setup (10 ACs)

**AC 1.4-1:** GitHub Actions workflow file created (.github/workflows/ci.yml) triggered on pull requests and pushes to main branch

**AC 1.4-2:** Workflow includes separate jobs for frontend and backend testing

**AC 1.4-3:** Frontend job runs: npm install, npm run lint, npm run test (with code coverage reporting)

**AC 1.4-4:** Backend job runs: Gradle build, unit tests, integration tests (with TestContainers for PostgreSQL)

**AC 1.4-5:** Test coverage reports generated and visible in workflow summary (minimum 70% backend, 60% frontend)

**AC 1.4-6:** Workflow fails if any tests fail or code coverage drops below thresholds

**AC 1.4-7:** Build artifacts (JAR file for backend, production build for frontend) generated and stored as workflow artifacts

**AC 1.4-8:** Docker images built as part of pipeline to validate Dockerfiles

**AC 1.4-9:** Workflow includes caching for npm dependencies and Gradle dependencies to speed up builds

**AC 1.4-10:** Status badge added to README.md showing build status

### Story 1.5: User Registration Backend API (11 ACs)

**AC 1.5-1:** POST /api/auth/register endpoint created accepting JSON body with email and password fields

**AC 1.5-2:** Request DTO (RegisterRequest) created with validation annotations: email must be valid format, password must be minimum 8 characters

**AC 1.5-3:** Email uniqueness validation enforced - returns 400 Bad Request if email already exists with appropriate error message

**AC 1.5-4:** Password hashed using bcrypt with work factor of 10 before storage

**AC 1.5-5:** User entity created and persisted to database with generated UUID, hashed password, and timestamps

**AC 1.5-6:** Successful registration returns 201 Created with user response DTO (id, email, createdAt) - password NOT included in response

**AC 1.5-7:** Service layer (AuthService) implements registration logic with proper transaction management

**AC 1.5-8:** Controller includes appropriate error handling for validation failures and duplicate emails

**AC 1.5-9:** Unit tests cover AuthService registration logic with mocked repository

**AC 1.5-10:** Integration test validates end-to-end registration flow including database persistence

**AC 1.5-11:** API documented with OpenAPI/Swagger annotations

### Story 1.6: User Login and JWT Authentication (12 ACs)

**AC 1.6-1:** POST /api/auth/login endpoint created accepting JSON body with email and password fields

**AC 1.6-2:** LoginRequest DTO created with validation for required fields

**AC 1.6-3:** User credentials validated against stored bcrypt hash

**AC 1.6-4:** Failed login (invalid email or password) returns 401 Unauthorized with generic "Invalid credentials" message (no email enumeration)

**AC 1.6-5:** Successful login generates JWT access token with claims: user ID, email, issued-at, expiration (24 hours)

**AC 1.6-6:** JWT token signed using secure secret key (loaded from environment variable, not hardcoded)

**AC 1.6-7:** Login response includes access token and user information (id, email)

**AC 1.6-8:** JwtService utility class created for token generation and validation

**AC 1.6-9:** Spring Security configured with JWT authentication filter for validating tokens on subsequent requests

**AC 1.6-10:** Unit tests cover login logic including success and failure scenarios

**AC 1.6-11:** Integration test validates full login flow and token generation

**AC 1.6-12:** API documented with OpenAPI/Swagger annotations

### Story 1.7: Frontend Authentication UI and State Management (14 ACs)

**AC 1.7-1:** Angular routing configured with routes for /login and /register

**AC 1.7-2:** LoginComponent created with reactive form including email (email validator) and password (required validator) fields

**AC 1.7-3:** RegisterComponent created with reactive form including email, password, and confirm password (matching validator) fields

**AC 1.7-4:** AuthService (Angular) created with methods: register(), login(), logout(), isAuthenticated()

**AC 1.7-5:** Login form submission calls backend /api/auth/login, stores JWT token in localStorage, and navigates to /dashboard on success

**AC 1.7-6:** Register form submission calls backend /api/auth/register, automatically logs in user, and navigates to /dashboard on success

**AC 1.7-7:** Form validation errors displayed inline below each field with user-friendly messages

**AC 1.7-8:** API error responses (400, 401) handled and displayed to user with appropriate error messages

**AC 1.7-9:** Loading states shown during API calls (disable submit button, show spinner)

**AC 1.7-10:** Auth guard (Angular guard) created to protect routes requiring authentication (redirects to /login if not authenticated)

**AC 1.7-11:** HTTP interceptor created to automatically attach JWT token to all outgoing API requests in Authorization header

**AC 1.7-12:** Logout functionality clears token from localStorage and redirects to /login

**AC 1.7-13:** Components follow Angular Material or Tailwind CSS styling for professional appearance

**AC 1.7-14:** Forms are fully keyboard accessible and include proper labels for screen readers

### Story 1.8: Deployment Configuration and Documentation (10 ACs)

**AC 1.8-1:** Environment-specific configuration files created: application-dev.yml, application-staging.yml, application-prod.yml

**AC 1.8-2:** Secrets management documented with examples (environment variables for JWT secret, database credentials)

**AC 1.8-3:** Production Dockerfile optimizations implemented (multi-stage builds, minimal base images, non-root user)

**AC 1.8-4:** Health check endpoint created: GET /api/health returns 200 OK with application status

**AC 1.8-5:** CORS configuration properly set for production (whitelist specific origins, not wildcard)

**AC 1.8-6:** docs/architecture/deployment.md created documenting deployment process, environment variables, and infrastructure requirements

**AC 1.8-7:** docs/architecture/tech-stack.md created listing all technologies, frameworks, and versions

**AC 1.8-8:** docs/architecture/source-tree.md created explaining repository structure and where to find key components

**AC 1.8-9:** Root README.md includes: project overview, prerequisites, local setup instructions, running tests, Docker commands, and links to detailed documentation

**AC 1.8-10:** BMAD standards checklist completed and documented in docs/

### Story 1.9: User Profile Management (21 ACs)

**AC 1.9-1:** GET /api/users/profile endpoint created returning authenticated user's profile (id, email, createdAt, updatedAt)

**AC 1.9-2:** Endpoint secured with JWT authentication - users can only access their own profile

**AC 1.9-3:** PUT /api/users/profile/email endpoint created accepting new email with validation

**AC 1.9-4:** Email update validates uniqueness and returns 400 Bad Request if email already exists

**AC 1.9-5:** Email update requires valid email format validation

**AC 1.9-6:** PUT /api/users/profile/password endpoint created accepting currentPassword and newPassword

**AC 1.9-7:** Password update validates current password matches stored hash before allowing change

**AC 1.9-8:** New password validated for minimum 8 characters and hashed with bcrypt before storage

**AC 1.9-9:** Failed password verification returns 401 Unauthorized with "Current password is incorrect" message

**AC 1.9-10:** ProfileService created in backend with methods: getProfile(), updateEmail(), updatePassword()

**AC 1.9-11:** Frontend ProfileComponent created with route /profile (protected by auth guard)

**AC 1.9-12:** Profile page displays current email and account creation date in read-only fields

**AC 1.9-13:** "Change Email" section with form field for new email and submit button

**AC 1.9-14:** "Change Password" section with fields for current password, new password, and confirm new password

**AC 1.9-15:** Password change form validates new password matches confirmation field

**AC 1.9-16:** All form submissions show loading states and display success/error messages

**AC 1.9-17:** Successful email or password update shows success notification and refreshes displayed data

**AC 1.9-18:** Profile page follows Angular Material/Tailwind CSS styling consistent with rest of application

**AC 1.9-19:** Unit tests cover ProfileService logic for all update scenarios (success, validation failures, authorization)

**AC 1.9-20:** Integration tests validate end-to-end profile update flows including authentication and database persistence

**AC 1.9-21:** API endpoints documented with OpenAPI/Swagger annotations

---

**Total Epic 1 Acceptance Criteria: 107**

## Traceability Mapping

This table maps each acceptance criterion to its corresponding technical specification sections, implementation components, and test approach, ensuring complete coverage and traceable requirements.

| AC ID | Spec Section(s) | Component/API/Artifact | Test Approach |
|-------|-----------------|------------------------|---------------|
| **Story 1.1: Project Initialization and Repository Structure** |
| 1.1-1 | System Architecture Alignment | Monorepo root structure (frontend/, backend/) | Manual verification of directory structure |
| 1.1-2 | Frontend Dependencies | Angular CLI project in frontend/ | Integration test: ng serve successful |
| 1.1-3 | Backend Dependencies | Spring Boot project in backend/ | Integration test: ./gradlew bootRun successful |
| 1.1-4 | - | README.md | Manual review of documentation completeness |
| 1.1-5 | - | .gitignore | Unit test: Verify excluded files not tracked |
| 1.1-6 | Internal Integration Points | Angular DevServer, Spring Boot embedded Tomcat | Integration test: Both servers start independently |
| 1.1-7 | Backend Dependencies | Gradle Wrapper (gradlew, gradlew.bat) | E2E test: ./gradlew build succeeds on fresh clone |
| 1.1-8 | Frontend Dependencies | package.json | Unit test: Verify Angular 17+ listed in dependencies |
| 1.1-9 | - | docs/ directory structure | Manual verification of BMAD structure |
| **Story 1.2: Docker Containerization and Local Development Environment** |
| 1.2-1 | Infrastructure Dependencies | frontend/Dockerfile | Integration test: docker build succeeds, image runs |
| 1.2-2 | Infrastructure Dependencies | backend/Dockerfile | Integration test: docker build succeeds, JAR executes |
| 1.2-3 | Docker Container Networking | docker-compose.yml | Integration test: docker-compose up starts 3 services |
| 1.2-4 | Infrastructure Dependencies | PostgreSQL service, Docker volume | Integration test: Data persists after container restart |
| 1.2-5 | Backend ↔ Database Integration | Environment variables (POSTGRES_HOST, etc.) | Integration test: Backend connects via env vars |
| 1.2-6 | Frontend ↔ Backend Integration | NGINX proxy config or Angular proxy.conf.json | E2E test: Frontend API calls reach backend |
| 1.2-7 | Docker Container Networking | docker-compose.yml (all services) | E2E test: http://localhost accessible, displays UI |
| 1.2-8 | - | Docker volume mounts, Spring DevTools | Manual test: Code changes reflect without rebuild |
| 1.2-9 | - | Health checks in docker-compose.yml | Integration test: Services report healthy status |
| 1.2-10 | - | README.md Docker section | Manual review of documentation |
| **Story 1.3: Database Schema and Migration Framework** |
| 1.3-1 | Database Migration | build.gradle (Flyway dependency) | Unit test: Flyway bean auto-configured |
| 1.3-2 | Data Models and Contracts | V1__initial_schema.sql | Integration test: Migration creates 3 tables |
| 1.3-3 | Data Models (User Entity) | users table schema | Integration test: User record inserted successfully |
| 1.3-4 | - | categories table, seed data migration | Integration test: 11 categories exist after migration |
| 1.3-5 | - | transactions table schema | Integration test: Transaction with FK constraints created |
| 1.3-6 | Performance (Database) | Database indexes (email, user_id, etc.) | Integration test: EXPLAIN ANALYZE shows index usage |
| 1.3-7 | Database Migration | Flyway auto-run configuration | Integration test: Migrations execute on startup |
| 1.3-8 | Data Models and Contracts | JPA entities (User, Transaction, Category) | Unit test: Entity validation annotations work |
| 1.3-9 | Services and Modules | UserRepository, TransactionRepository, CategoryRepository | Integration test: Repository CRUD operations |
| 1.3-10 | Backend ↔ Database Integration | Application startup logs | Integration test: No migration errors in logs |
| **Story 1.4: CI/CD Pipeline Setup** |
| 1.4-1 | - | .github/workflows/ci.yml | Integration test: Workflow triggers on PR |
| 1.4-2 | - | CI workflow jobs (frontend-test, backend-test) | Integration test: Both jobs execute in parallel |
| 1.4-3 | Frontend Dependencies, Testing | Frontend CI job steps | Integration test: Lint and test pass in CI |
| 1.4-4 | Backend Dependencies, Testing | Backend CI job with TestContainers | Integration test: Tests run with PostgreSQL container |
| 1.4-5 | Test Strategy Summary | Coverage reporting (JaCoCo, Karma) | Integration test: Coverage artifacts generated |
| 1.4-6 | - | CI workflow failure conditions | Integration test: Workflow fails on low coverage |
| 1.4-7 | - | Workflow artifacts (JAR, dist/) | Integration test: Artifacts downloadable from workflow |
| 1.4-8 | Infrastructure Dependencies | Docker build steps in workflow | Integration test: Images build successfully in CI |
| 1.4-9 | - | Workflow caching configuration | Integration test: Cache hit on second run |
| 1.4-10 | - | README.md badge | Manual verification of build status badge |
| **Story 1.5: User Registration Backend API** |
| 1.5-1 | APIs and Interfaces (POST /api/auth/register) | AuthController.register() | Integration test: Endpoint accessible, accepts JSON |
| 1.5-2 | Data Models (RegisterRequest DTO) | RegisterRequest class | Unit test: Validation failures for invalid input |
| 1.5-3 | Error Handling, Data Consistency | AuthService email uniqueness check | Integration test: 400 returned for duplicate email |
| 1.5-4 | Security (Password Security) | AuthService.hashPassword() | Unit test: BCrypt hash verifies correctly |
| 1.5-5 | Data Models (User Entity), Workflows (Registration) | User entity, UserRepository | Integration test: User persisted to database |
| 1.5-6 | APIs and Interfaces (AuthResponse) | UserResponse DTO | Unit test: Response excludes passwordHash field |
| 1.5-7 | Services and Modules (AuthService) | AuthService class | Unit test: Transaction rollback on error |
| 1.5-8 | Error Handling | AuthController exception handlers | Integration test: Proper error responses returned |
| 1.5-9 | Test Strategy Summary | AuthService unit tests | Unit test: Mocked repository interactions |
| 1.5-10 | Test Strategy Summary | Registration integration tests | Integration test: Full flow with TestContainers |
| 1.5-11 | - | OpenAPI annotations | Manual review: Swagger UI documentation |
| **Story 1.6: User Login and JWT Authentication** |
| 1.6-1 | APIs and Interfaces (POST /api/auth/login) | AuthController.login() | Integration test: Endpoint accessible, accepts credentials |
| 1.6-2 | Data Models (LoginRequest DTO) | LoginRequest class | Unit test: Validation enforces required fields |
| 1.6-3 | Security (Password Security) | AuthService.verifyPassword() | Unit test: BCrypt verification logic |
| 1.6-4 | Error Handling, Security | Login failure handling | Integration test: 401 for invalid credentials |
| 1.6-5 | Data Models (JWT Token Structure), Workflows (Login) | JwtService.generateToken() | Unit test: Token includes correct claims, 24h expiry |
| 1.6-6 | Security (JWT Token Security) | JWT secret from environment | Unit test: Token generation uses env var secret |
| 1.6-7 | APIs and Interfaces (AuthResponse) | AuthResponse DTO | Integration test: Response includes token and user |
| 1.6-8 | Services and Modules (JwtService) | JwtService class | Unit test: Token validation succeeds/fails correctly |
| 1.6-9 | Security (Security Architecture) | JwtAuthenticationFilter, SecurityConfig | Integration test: Protected endpoint rejects invalid token |
| 1.6-10 | Test Strategy Summary | Login unit tests | Unit test: Success and failure scenarios covered |
| 1.6-11 | Test Strategy Summary | Login integration tests | Integration test: Full authentication flow |
| 1.6-12 | - | OpenAPI annotations | Manual review: Swagger UI documentation |
| **Story 1.7: Frontend Authentication UI and State Management** |
| 1.7-1 | Component-Based Frontend | Angular routing module | E2E test: Navigate to /login and /register |
| 1.7-2 | Component-Based Frontend | LoginComponent | Unit test: Form validators enforce email/password |
| 1.7-3 | Component-Based Frontend | RegisterComponent | Unit test: Password confirmation validator works |
| 1.7-4 | Services and Modules (AuthService Angular) | AuthService class | Unit test: Methods return expected observables |
| 1.7-5 | Workflows (User Login Flow) | LoginComponent.onSubmit() | E2E test: Successful login navigates to dashboard |
| 1.7-6 | Workflows (User Registration Flow) | RegisterComponent.onSubmit() | E2E test: Registration logs in and navigates |
| 1.7-7 | Component-Based Frontend | Reactive form error display | E2E test: Validation errors shown inline |
| 1.7-8 | Error Handling (Frontend) | HTTP error handling in components | E2E test: API errors displayed to user |
| 1.7-9 | Frontend ↔ Backend Integration | Loading state management | E2E test: Submit button disabled during call |
| 1.7-10 | Services and Modules (AuthGuard) | AuthGuard class | E2E test: Unauthenticated user redirected to login |
| 1.7-11 | Services and Modules (JwtInterceptor) | JwtInterceptor class | Integration test: Token attached to requests |
| 1.7-12 | Workflows (Logout Flow) | AuthService.logout() | E2E test: Logout clears token, redirects |
| 1.7-13 | Frontend Dependencies (Angular Material/Tailwind) | Component templates | Manual review: Visual consistency |
| 1.7-14 | - | ARIA labels, keyboard navigation | Accessibility test: Screen reader and keyboard |
| **Story 1.8: Deployment Configuration and Documentation** |
| 1.8-1 | - | application-dev/staging/prod.yml | Manual review: Environment-specific configs exist |
| 1.8-2 | Security (Data Protection) | Secrets documentation | Manual review: Environment variable examples |
| 1.8-3 | Infrastructure Dependencies | Production Dockerfiles | Security scan: Non-root user, minimal image |
| 1.8-4 | APIs and Interfaces (GET /api/health) | HealthController | Integration test: /api/health returns 200 OK |
| 1.8-5 | Security (CORS Configuration) | SecurityConfig CORS settings | Integration test: CORS headers correct for origin |
| 1.8-6 | - | docs/architecture/deployment.md | Manual review: Documentation completeness |
| 1.8-7 | Dependencies and Integrations | docs/architecture/tech-stack.md | Manual review: All dependencies listed with versions |
| 1.8-8 | System Architecture Alignment | docs/architecture/source-tree.md | Manual review: Structure explanation accuracy |
| 1.8-9 | - | README.md | Manual review: All sections complete |
| 1.8-10 | - | BMAD checklist in docs/ | Manual review: Standards compliance |
| **Story 1.9: User Profile Management** |
| 1.9-1 | APIs and Interfaces (GET /api/users/profile) | UserController.getProfile() | Integration test: Profile data returned correctly |
| 1.9-2 | Security (Authorization Rules) | JWT authentication on endpoint | Integration test: 401 without token, 200 with token |
| 1.9-3 | APIs and Interfaces (PUT /api/users/profile/email) | UserController.updateEmail() | Integration test: Email update succeeds |
| 1.9-4 | Error Handling, Data Consistency | Email uniqueness validation | Integration test: 400 for duplicate email |
| 1.9-5 | Data Models (UpdateEmailRequest DTO) | UpdateEmailRequest validation | Unit test: Invalid email format rejected |
| 1.9-6 | APIs and Interfaces (PUT /api/users/profile/password) | UserController.updatePassword() | Integration test: Password change succeeds |
| 1.9-7 | Security (Password Security), Workflows (Password Update) | Password verification logic | Integration test: Incorrect current password fails |
| 1.9-8 | Security (Password Security) | Password hashing on update | Unit test: New password hashed with BCrypt |
| 1.9-9 | Error Handling | Password verification error handling | Integration test: 401 with specific error message |
| 1.9-10 | Services and Modules (UserService) | UserService class | Unit test: All methods with success/failure paths |
| 1.9-11 | Component-Based Frontend | ProfileComponent, routing | E2E test: /profile route accessible when authenticated |
| 1.9-12 | Component-Based Frontend | ProfileComponent template | E2E test: Email and createdAt displayed |
| 1.9-13 | Component-Based Frontend | Email change form | E2E test: Form submission updates email |
| 1.9-14 | Component-Based Frontend | Password change form | E2E test: Form has 3 password fields |
| 1.9-15 | Component-Based Frontend | Password confirmation validator | Unit test: Validator fails on mismatch |
| 1.9-16 | Frontend ↔ Backend Integration | Loading and error states | E2E test: Spinner shown, messages displayed |
| 1.9-17 | Component-Based Frontend | Success notifications | E2E test: Notification shown, data refreshed |
| 1.9-18 | Frontend Dependencies (Angular Material/Tailwind) | ProfileComponent styling | Manual review: Consistent design |
| 1.9-19 | Test Strategy Summary | ProfileService unit tests | Unit test: All scenarios with mocked dependencies |
| 1.9-20 | Test Strategy Summary | Profile integration tests | Integration test: E2E flows with TestContainers |
| 1.9-21 | - | OpenAPI annotations | Manual review: Swagger UI documentation |

---

**Coverage Summary:**
- **107 ACs mapped** to technical specification sections
- **All components and APIs** traced to requirements
- **Test strategy defined** for each acceptance criterion (Unit/Integration/E2E/Manual)
- **100% traceability** from PRD requirements → Epic ACs → Tech Spec → Tests

## Risks, Assumptions, Open Questions

### Risks

**RISK-1: Docker Environment Setup Complexity (Medium)**
- **Description:** New developers may struggle with Docker installation and configuration, especially on Windows with WSL2 requirements
- **Impact:** Delayed onboarding, inconsistent development environments
- **Mitigation:**
  - Comprehensive Docker setup documentation with OS-specific instructions
  - Pre-build validation script to check Docker version, memory allocation, and WSL2 configuration
  - Team pair programming sessions for Docker newcomers
- **Owner:** DevOps/Team Lead

**RISK-2: JWT Secret Key Management in Production (High)**
- **Description:** Improper JWT secret key management could lead to security vulnerabilities (hardcoded secrets, weak keys, exposure in logs)
- **Impact:** Authentication bypass, unauthorized access to user accounts
- **Mitigation:**
  - Enforce environment variable loading for JWT_SECRET (fail fast if not present)
  - Generate cryptographically strong secret (minimum 256 bits)
  - Document secret rotation procedure in deployment.md
  - Add automated check in CI/CD to prevent committing secrets to repository
- **Owner:** Security Lead, Backend Team

**RISK-3: Database Migration Failures in CI/CD (Medium)**
- **Description:** Flyway migrations may fail in CI/CD due to timing issues, TestContainers startup delays, or schema conflicts
- **Impact:** Pipeline failures, delayed deployments, blocked merges
- **Mitigation:**
  - Implement retry logic for TestContainers database connection
  - Add health check polling before running migrations in tests
  - Create rollback procedures for failed migrations
  - Test migrations on clean database in CI (no state carryover between runs)
- **Owner:** Backend Team, CI/CD Maintainer

**RISK-4: CORS Configuration Issues Between Frontend and Backend (Low)**
- **Description:** CORS misconfiguration could block API requests or expose application to cross-origin attacks
- **Impact:** Frontend unable to communicate with backend, potential security vulnerability
- **Mitigation:**
  - Explicitly whitelist allowed origins (no wildcards in production)
  - Test CORS configuration in integration tests with preflight requests
  - Document CORS settings in deployment guide for each environment
  - Use environment-specific configuration (localhost for dev, production domain for prod)
- **Owner:** Full-Stack Team

**RISK-5: TestContainers Resource Requirements in CI/CD (Medium)**
- **Description:** GitHub Actions runners may have insufficient resources or Docker restrictions for TestContainers
- **Impact:** Integration test failures, slow CI/CD pipelines
- **Mitigation:**
  - Verify GitHub Actions supports Docker-in-Docker for TestContainers
  - Use lightweight PostgreSQL Alpine image to reduce resource usage
  - Implement parallel test execution with resource pooling
  - Consider alternative: H2 in-memory database for CI, PostgreSQL for local development
- **Owner:** Backend Team, CI/CD Maintainer

### Assumptions

**ASSUMPTION-1: Developer Environment Prerequisites**
- All developers have Docker 24+ installed and running on their local machines
- Team members have basic familiarity with Docker Compose commands
- Windows developers are using WSL2 for Docker support

**ASSUMPTION-2: CI/CD Platform Capabilities**
- GitHub Actions runners support Docker execution (ubuntu-latest image)
- Sufficient runner resources for parallel frontend/backend test jobs
- Workflow artifact storage available for JAR files and build outputs

**ASSUMPTION-3: Database Compatibility and Features**
- PostgreSQL 15+ provides all required features (UUID generation, JSONB if needed in future)
- Flyway 10.4+ compatible with PostgreSQL 15+ without schema migration issues
- Database connection pooling defaults (HikariCP 10 connections) sufficient for MVP load

**ASSUMPTION-4: Authentication and Security Requirements**
- 24-hour JWT token expiration acceptable for MVP (no refresh token needed initially)
- BCrypt work factor 10 provides adequate security without performance degradation
- HTTPS enforcement in production is responsibility of deployment environment (load balancer/ingress)

**ASSUMPTION-5: Technology Stack Stability**
- Angular 17+ and Spring Boot 3.2+ are production-ready with stable APIs
- No breaking changes expected in minor version updates during Epic 1 implementation
- Community support and documentation available for chosen technology stack

**ASSUMPTION-6: Scope and Timeline**
- Epic 1 can be completed within planned sprint capacity (9 stories)
- No external dependencies or third-party API integrations required for Epic 1
- User authentication sufficient for MVP; advanced features (MFA, OAuth) deferred post-MVP

### Open Questions

**QUESTION-1: Production Hosting Platform**
- **Question:** Which cloud provider will be used for production deployment (AWS, Azure, GCP, or on-premises)?
- **Impact:** Infrastructure configuration, environment variables, HTTPS/SSL setup, database hosting
- **Decision Needed By:** Before Story 1.8 (Deployment Configuration)
- **Stakeholders:** Product Owner, Infrastructure Team, Budget Approver

**QUESTION-2: SSL/TLS Certificate Strategy**
- **Question:** How will SSL certificates be provisioned and managed (Let's Encrypt, cloud provider, purchased certificates)?
- **Impact:** HTTPS enforcement configuration, certificate renewal automation
- **Decision Needed By:** Before Story 1.8 (Deployment Configuration)
- **Stakeholders:** DevOps Team, Security Team

**QUESTION-3: Database Backup and Disaster Recovery**
- **Question:** What is the backup frequency, retention policy, and disaster recovery RTO/RPO for production database?
- **Impact:** Database volume configuration, backup automation, restore procedures
- **Decision Needed By:** Before production deployment (post-Epic 1)
- **Stakeholders:** Database Administrator, Product Owner, Compliance Team

**QUESTION-4: Monitoring and Alerting Requirements**
- **Question:** What monitoring tools should be integrated (Prometheus, Grafana, CloudWatch, Datadog, New Relic)?
- **Impact:** Logging format (JSON structured logs), metrics endpoints, observability configuration
- **Decision Needed By:** Post-MVP enhancement (basic logging sufficient for Epic 1)
- **Stakeholders:** DevOps Team, SRE Team

**QUESTION-5: Email Service Provider for Future Features**
- **Question:** Which email service will be used for email verification and notifications (SendGrid, AWS SES, Mailgun)?
- **Impact:** Future Story dependencies, API integration, email template management
- **Decision Needed By:** Before Epic 2 or when email verification feature prioritized
- **Stakeholders:** Product Owner, Backend Team

## Test Strategy Summary

### Test Pyramid and Coverage Targets

Epic 1 follows a balanced test pyramid approach to ensure quality while maintaining development velocity:

**Coverage Targets:**
- Backend (Spring Boot): **70% minimum** line coverage (unit + integration tests)
- Frontend (Angular): **60% minimum** line coverage (unit + component tests)
- Critical paths (authentication, registration): **90%+** coverage required

**Test Distribution:**
- **Unit Tests (60% of test effort):** Fast, isolated tests of business logic
- **Integration Tests (30% of test effort):** Component interactions, database operations, API endpoints
- **E2E Tests (10% of test effort):** Critical user flows, cross-stack validation

### Backend Testing Strategy (Spring Boot)

**Unit Tests (JUnit 5 + Mockito):**
- **Service Layer:** All business logic methods with mocked repositories
  - AuthService: Registration, login, password hashing, token generation
  - UserService: Profile updates, email uniqueness validation, password verification
  - JwtService: Token generation, validation, expiration handling
- **DTO Validation:** Bean Validation annotations enforced
  - RegisterRequest, LoginRequest, UpdateEmailRequest, UpdatePasswordRequest
- **Test Coverage:** Positive scenarios, edge cases, error conditions

**Integration Tests (Spring Boot Test + TestContainers):**
- **Repository Layer:** JPA entities and database interactions
  - UserRepository: CRUD operations, findByEmail queries
  - TransactionRepository: Foreign key constraints, cascading deletes
  - CategoryRepository: Seed data verification
- **API Endpoints (Controller Layer):** Full request/response cycle
  - POST /api/auth/register: Success (201), duplicate email (400), validation errors (400)
  - POST /api/auth/login: Success (200), invalid credentials (401)
  - GET /api/users/profile: Authenticated (200), unauthenticated (401)
  - PUT /api/users/profile/email: Success (200), duplicate (400), unauthorized (401)
  - PUT /api/users/profile/password: Success (200), wrong password (401)
- **Security:** JWT authentication filter, CORS preflight requests
- **Database Migrations:** Flyway executes successfully, schema matches entities

**TestContainers Configuration:**
- PostgreSQL 15 Alpine image for lightweight, fast startup
- Automatic container lifecycle management (start before tests, stop after)
- Isolated database per test class to prevent test interdependencies

### Frontend Testing Strategy (Angular)

**Unit Tests (Jasmine + Karma):**
- **Services:** Angular service methods with mocked HTTP calls
  - AuthService: register(), login(), logout(), isAuthenticated()
  - UserService: getProfile(), updateEmail(), updatePassword()
- **Components:** Component logic and reactive forms
  - LoginComponent: Form validators (email, password required)
  - RegisterComponent: Password confirmation matching validator
  - ProfileComponent: Email and password change form submission
- **Guards and Interceptors:**
  - AuthGuard: Redirect to login when unauthenticated
  - JwtInterceptor: Attach Authorization header to requests
- **Test Coverage:** Component initialization, form validation, method calls, error handling

**Integration/E2E Tests (Cypress or Playwright - Optional for Epic 1):**
- **Authentication Flows:**
  - User registration → automatic login → dashboard navigation
  - User login → JWT stored in localStorage → dashboard access
  - Logout → token cleared → redirect to login
- **Protected Routes:** AuthGuard prevents unauthenticated access
- **API Integration:** Frontend successfully communicates with backend via Docker network
- **UI Interactions:** Form submissions, error messages display, loading states

**Component Tests (Angular Testing Library - Alternative):**
- Focused tests for template rendering and user interactions
- Accessibility validation (ARIA labels, keyboard navigation)

### CI/CD Pipeline Testing

**GitHub Actions Workflow (.github/workflows/ci.yml):**

**Frontend Job:**
```yaml
- npm install (with dependency caching)
- npm run lint (ESLint + Prettier)
- npm run test (Jasmine/Karma with code coverage)
- Generate coverage report (Codecov or built-in)
- Build production artifact (ng build --prod)
```

**Backend Job:**
```yaml
- Gradle build (with dependency caching)
- ./gradlew test (unit tests)
- ./gradlew integrationTest (TestContainers)
- Generate coverage report (JaCoCo)
- Build JAR artifact
- Docker image build (validate Dockerfile)
```

**Quality Gates:**
- Tests must pass (exit code 0)
- Coverage thresholds enforced (backend 70%, frontend 60%)
- Linting errors block merge
- Build artifacts must be generated successfully

### Security Testing

**Static Analysis:**
- **Dependency Scanning:** npm audit (frontend), Gradle dependency-check (backend)
- **Secret Detection:** Automated scan for committed secrets (GitHub Advanced Security or git-secrets)
- **Docker Image Scanning:** Trivy or Snyk scan for vulnerabilities in base images

**Security-Specific Tests:**
- **Password Hashing:** Unit test verifies BCrypt with work factor 10
- **JWT Security:** Integration test validates token signature, expiration enforcement
- **SQL Injection Prevention:** Integration test attempts SQL injection via form inputs (parameterized queries protect)
- **XSS Prevention:** E2E test verifies Angular sanitization prevents script injection
- **CORS:** Integration test validates preflight requests and origin whitelisting

### Performance Testing (Baseline Establishment)

**JMeter Load Tests (Optional for Epic 1, Recommended for Epic 2):**
- **Scenario:** 100 concurrent users performing registration and login
- **Metrics:** Response time p95 < 500ms, error rate < 1%
- **Goal:** Establish baseline performance metrics for future comparison

**Lighthouse Audits (Frontend):**
- Run Lighthouse CI on production build
- Target score: Performance 90+, Accessibility 90+, Best Practices 90+

### Test Data Management

**Database Seed Data:**
- Flyway migration includes 11 predefined categories (Salary, Rent, etc.)
- Test fixtures for known user accounts (test@example.com) with predictable passwords

**Test Isolation:**
- Integration tests use TestContainers with isolated database instances
- No shared state between test classes
- Transactional rollback for tests that modify database

### Manual Testing Checklist

**Smoke Tests (Post-Deployment):**
- [ ] Docker Compose starts all services without errors
- [ ] Frontend accessible at http://localhost (or production URL)
- [ ] Backend health endpoint returns 200 OK
- [ ] User can register new account
- [ ] User can login with created account
- [ ] Protected routes redirect to login when unauthenticated
- [ ] User can update email and password

**Accessibility Testing:**
- [ ] Screen reader compatibility (NVDA/JAWS)
- [ ] Keyboard navigation (Tab, Enter, Escape)
- [ ] ARIA labels present on form fields

**Browser Compatibility (Frontend):**
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Safari (latest)
- [ ] Edge (latest)

### Continuous Improvement

**Metrics to Track:**
- Test execution time (optimize slow tests)
- Test flakiness rate (quarantine or fix flaky tests)
- Code coverage trends (enforce upward trajectory)
- Defect escape rate (bugs found in production vs. caught in tests)

**Post-Epic 1 Enhancements:**
- Add E2E tests with Cypress or Playwright for critical user journeys
- Implement contract testing for frontend-backend API agreements
- Integrate mutation testing (PIT for backend, Stryker for frontend)
- Add visual regression testing for UI components
