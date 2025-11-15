# Epic 1: Foundation & Core Infrastructure

**Epic Goal:**
Establish the complete technical foundation for the Smart Budget App including project structure, development environment setup, containerization, CI/CD pipeline, database configuration, and user authentication. This epic delivers a fully deployable application with working registration and login functionality, setting the stage for all business features while validating the development workflow and deployment process.

## Story 1.1: Project Initialization and Repository Structure

As a developer,
I want the monorepo project structure initialized with both Angular and Spring Boot applications,
so that the team has a consistent foundation to build features and can start development immediately.

**Acceptance Criteria:**

1. Monorepo directory structure created with `frontend/` and `backend/` subdirectories
2. Angular 17+ application generated using Angular CLI in `frontend/` directory with TypeScript strict mode enabled
3. Spring Boot 3.x application generated (using Spring Initializr or Gradle init) in `backend/` directory with Java 21, Spring Web, Spring Data JPA, Spring Security, and PostgreSQL dependencies
4. Root-level README.md created with project overview, technology stack, and basic getting started instructions
5. .gitignore configured for both Node.js and Java/Gradle artifacts
6. Both frontend and backend applications can run independently (Angular dev server on port 4200, Spring Boot on port 8080)
7. Gradle wrapper included in backend/ for consistent build experience
8. npm/pnpm package.json includes Angular 17+ and required dependencies
9. Basic folder structure follows BMAD standards with docs/ directory created

## Story 1.2: Docker Containerization and Local Development Environment

As a developer,
I want Docker containers and docker-compose configuration for the entire application stack,
so that I can run the full application (frontend, backend, database) with a single command and ensure consistency across all development environments.

**Acceptance Criteria:**

1. Dockerfile created for Angular frontend (multi-stage build: npm build → nginx serving static files)
2. Dockerfile created for Spring Boot backend (multi-stage build: Gradle build → Java runtime)
3. docker-compose.yml orchestrates three services: frontend, backend, and PostgreSQL database
4. PostgreSQL service configured with volume mounting for data persistence and initialization scripts
5. Environment variables properly configured for database connection (host, port, username, password)
6. Frontend container proxies API requests to backend container (NGINX configuration or Angular proxy.conf.json)
7. Running `docker-compose up` successfully starts all services and application is accessible at http://localhost
8. Development mode supports hot-reload for both frontend (volume mount) and backend (optional: Spring DevTools)
9. docker-compose.yml includes appropriate health checks for all services
10. README.md updated with Docker setup instructions and common commands

## Story 1.3: Database Schema and Migration Framework

As a developer,
I want the database schema designed and migration framework configured,
so that database changes are version-controlled, reproducible, and all core entities are properly modeled.

**Acceptance Criteria:**

1. Flyway configured as migration tool with dependencies added to build.gradle
2. Initial migration script created (V1__initial_schema.sql) defining users, transactions, and categories tables
3. Users table includes: id (UUID), email (unique), password_hash, created_at, updated_at
4. Categories table includes: id, name, type (income/expense), description with seed data for predefined categories (Salary, Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Savings, Investments, Other)
5. Transactions table includes: id (UUID), user_id (FK to users), amount (decimal), transaction_date, description, category_id (FK to categories), transaction_type (income/expense enum), created_at, updated_at
6. Appropriate indexes created on foreign keys and frequently queried columns (user_id, transaction_date, category_id)
7. Database migrations run automatically on application startup
8. JPA entity classes created in backend matching database schema with proper annotations (@Entity, @Table, @Column, relationships)
9. Repository interfaces created extending JpaRepository for User, Transaction, and Category entities
10. Application successfully connects to PostgreSQL and migrations execute without errors

## Story 1.4: CI/CD Pipeline Setup

As a developer,
I want automated CI/CD pipeline configured using GitHub Actions,
so that every code change is automatically tested, built, and validated before merging, ensuring code quality and reducing manual overhead.

**Acceptance Criteria:**

1. GitHub Actions workflow file created (.github/workflows/ci.yml) triggered on pull requests and pushes to main branch
2. Workflow includes separate jobs for frontend and backend testing
3. Frontend job runs: npm install, npm run lint, npm run test (with code coverage reporting)
4. Backend job runs: Gradle build, unit tests, integration tests (with TestContainers for PostgreSQL)
5. Test coverage reports generated and visible in workflow summary (minimum 70% backend, 60% frontend)
6. Workflow fails if any tests fail or code coverage drops below thresholds
7. Build artifacts (JAR file for backend, production build for frontend) generated and stored as workflow artifacts
8. Docker images built as part of pipeline to validate Dockerfiles
9. Workflow includes caching for npm dependencies and Gradle dependencies to speed up builds
10. Status badge added to README.md showing build status

## Story 1.5: User Registration Backend API

As a new user,
I want to register for an account with my email and password,
so that I can securely access the application and store my personal financial data.

**Acceptance Criteria:**

1. POST /api/auth/register endpoint created accepting JSON body with email and password fields
2. Request DTO (RegisterRequest) created with validation annotations: email must be valid format, password must be minimum 8 characters
3. Email uniqueness validation enforced - returns 400 Bad Request if email already exists with appropriate error message
4. Password hashed using bcrypt with work factor of 10 before storage
5. User entity created and persisted to database with generated UUID, hashed password, and timestamps
6. Successful registration returns 201 Created with user response DTO (id, email, createdAt) - password NOT included in response
7. Service layer (AuthService) implements registration logic with proper transaction management
8. Controller includes appropriate error handling for validation failures and duplicate emails
9. Unit tests cover AuthService registration logic with mocked repository
10. Integration test validates end-to-end registration flow including database persistence
11. API documented with OpenAPI/Swagger annotations

## Story 1.6: User Login and JWT Authentication

As a registered user,
I want to log in with my email and password and receive an authentication token,
so that I can securely access protected features and my personal data.

**Acceptance Criteria:**

1. POST /api/auth/login endpoint created accepting JSON body with email and password fields
2. LoginRequest DTO created with validation for required fields
3. User credentials validated against stored bcrypt hash
4. Failed login (invalid email or password) returns 401 Unauthorized with generic "Invalid credentials" message (no email enumeration)
5. Successful login generates JWT access token with claims: user ID, email, issued-at, expiration (24 hours)
6. JWT token signed using secure secret key (loaded from environment variable, not hardcoded)
7. Login response includes access token and user information (id, email)
8. JwtService utility class created for token generation and validation
9. Spring Security configured with JWT authentication filter for validating tokens on subsequent requests
10. Unit tests cover login logic including success and failure scenarios
11. Integration test validates full login flow and token generation
12. API documented with OpenAPI/Swagger annotations

## Story 1.7: Frontend Authentication UI and State Management

As a user,
I want a user interface to register and log in to the application,
so that I can access my account through an intuitive and visually appealing interface.

**Acceptance Criteria:**

1. Angular routing configured with routes for /login and /register
2. LoginComponent created with reactive form including email (email validator) and password (required validator) fields
3. RegisterComponent created with reactive form including email, password, and confirm password (matching validator) fields
4. AuthService (Angular) created with methods: register(), login(), logout(), isAuthenticated()
5. Login form submission calls backend /api/auth/login, stores JWT token in localStorage, and navigates to /dashboard on success
6. Register form submission calls backend /api/auth/register, automatically logs in user, and navigates to /dashboard on success
7. Form validation errors displayed inline below each field with user-friendly messages
8. API error responses (400, 401) handled and displayed to user with appropriate error messages
9. Loading states shown during API calls (disable submit button, show spinner)
10. Auth guard (Angular guard) created to protect routes requiring authentication (redirects to /login if not authenticated)
11. HTTP interceptor created to automatically attach JWT token to all outgoing API requests in Authorization header
12. Logout functionality clears token from localStorage and redirects to /login
13. Components follow Angular Material or Tailwind CSS styling for professional appearance
14. Forms are fully keyboard accessible and include proper labels for screen readers

## Story 1.8: Deployment Configuration and Documentation

As a developer,
I want deployment configuration and comprehensive documentation,
so that the application can be deployed to a cloud environment and new team members can onboard quickly.

**Acceptance Criteria:**

1. Environment-specific configuration files created: application-dev.yml, application-staging.yml, application-prod.yml
2. Secrets management documented with examples (environment variables for JWT secret, database credentials)
3. Production Dockerfile optimizations implemented (multi-stage builds, minimal base images, non-root user)
4. Health check endpoint created: GET /api/health returns 200 OK with application status
5. CORS configuration properly set for production (whitelist specific origins, not wildcard)
6. docs/architecture/deployment.md created documenting deployment process, environment variables, and infrastructure requirements
7. docs/architecture/tech-stack.md created listing all technologies, frameworks, and versions
8. docs/architecture/source-tree.md created explaining repository structure and where to find key components
9. Root README.md includes: project overview, prerequisites, local setup instructions, running tests, Docker commands, and links to detailed documentation
10. BMAD standards checklist completed and documented in docs/

## Story 1.9: User Profile Management

As a registered user,
I want to view and update my profile information including email and password,
so that I can maintain accurate account details and change my credentials when needed for security.

**Acceptance Criteria:**

1. GET /api/users/profile endpoint created returning authenticated user's profile (id, email, createdAt, updatedAt)
2. Endpoint secured with JWT authentication - users can only access their own profile
3. PUT /api/users/profile/email endpoint created accepting new email with validation
4. Email update validates uniqueness and returns 400 Bad Request if email already exists
5. Email update requires valid email format validation
6. PUT /api/users/profile/password endpoint created accepting currentPassword and newPassword
7. Password update validates current password matches stored hash before allowing change
8. New password validated for minimum 8 characters and hashed with bcrypt before storage
9. Failed password verification returns 401 Unauthorized with "Current password is incorrect" message
10. ProfileService created in backend with methods: getProfile(), updateEmail(), updatePassword()
11. Frontend ProfileComponent created with route /profile (protected by auth guard)
12. Profile page displays current email and account creation date in read-only fields
13. "Change Email" section with form field for new email and submit button
14. "Change Password" section with fields for current password, new password, and confirm new password
15. Password change form validates new password matches confirmation field
16. All form submissions show loading states and display success/error messages
17. Successful email or password update shows success notification and refreshes displayed data
18. Profile page follows Angular Material/Tailwind CSS styling consistent with rest of application
19. Unit tests cover ProfileService logic for all update scenarios (success, validation failures, authorization)
20. Integration tests validate end-to-end profile update flows including authentication and database persistence
21. API endpoints documented with OpenAPI/Swagger annotations
