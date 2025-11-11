# Technical Assumptions

## Repository Structure: Monorepo

The project will use a **monorepo** structure housing both the Angular frontend and Spring Boot backend in a single repository. This approach provides:

- **Simplified dependency management:** Shared TypeScript types and interfaces between frontend and backend (via API contract definitions)
- **Coordinated versioning:** Single source of truth for version control and release management
- **Easier code sharing:** Common utilities, constants, and configuration files accessible to both projects
- **Streamlined CI/CD:** Single pipeline can build, test, and deploy both applications together
- **Developer experience:** Single clone, single IDE workspace, easier onboarding

**Repository Structure:**
```
smart-budget-app/
├── frontend/          # Angular 17+ application
├── backend/           # Spring Boot Java 21 application
├── docs/              # BMAD documentation (PRD, architecture, etc.)
├── .github/           # GitHub Actions workflows
├── docker-compose.yml # Local development orchestration
└── README.md          # Project overview
```

## Service Architecture

**Monolithic Backend with Modular Design**

The backend will be a single Spring Boot application deployed as one service, but internally organized with clear modular boundaries:

- **Layered Architecture:**
  - **Controller Layer:** REST API endpoints handling HTTP requests/responses
  - **Service Layer:** Business logic and orchestration
  - **Repository Layer:** Data access using Spring Data JPA with Hibernate

- **Logical Modules:**
  - **Authentication Module:** User registration, login, JWT token management
  - **Transaction Module:** CRUD operations for transactions
  - **Category Module:** Category management and assignment
  - **Analytics Module:** Aggregation queries for dashboard summaries and charts
  - **AI Categorization Module:** Machine learning service for transaction classification

- **Rationale:** Monolithic approach is appropriate for MVP scope (single team, moderate complexity, predictable scaling needs). The modular internal structure provides clear boundaries that could support future microservices extraction if needed.

**Frontend as Single-Page Application (SPA):**
- Angular 17+ SPA communicating with backend via RESTful APIs
- Client-side routing for navigation without page reloads
- State management using Angular services and RxJS observables
- Lazy loading for feature modules to optimize initial load time

## Testing Requirements

**Comprehensive Testing Strategy:**

**Backend (Java/Spring Boot):**
- **Unit Tests (Required):** JUnit 5 and Mockito for testing service layer business logic in isolation; target 70%+ code coverage
- **Integration Tests (Required):** Spring Boot Test with TestContainers for PostgreSQL to test repository layer and database interactions
- **API Tests (Required):** MockMvc or RestAssured for testing controller endpoints and request/response handling
- **Test Organization:** Separate source sets (`src/test/java`) with consistent naming convention (`*Test.java` for unit tests, `*IntegrationTest.java` for integration tests)

**Frontend (Angular/TypeScript):**
- **Unit Tests (Required):** Jasmine/Karma for testing components, services, and utilities in isolation; target 60%+ code coverage
- **Component Tests (Required):** Angular TestBed for testing component interactions with templates and dependencies
- **E2E Tests (Phase 2):** Cypress or Playwright for critical user flows (login, add transaction, view dashboard) - NOT required for MVP but infrastructure should support future addition

**Manual Testing:**
- **Developer Testing:** Each story includes manual verification checklist before marking complete
- **Beta Testing:** Structured beta testing with 20+ users during final MVP validation phase
- **Convenience Methods:** Development environment includes seed data scripts and test user accounts for rapid manual testing

**CI/CD Integration:**
- All tests run automatically on every pull request via GitHub Actions
- Build fails if tests fail or coverage drops below thresholds
- Deployment to staging/production requires passing test suite

## Additional Technical Assumptions and Requests

**Build and Dependency Management:**
- **Frontend:** npm (or pnpm) for package management, Angular CLI for build tooling
- **Backend:** Gradle 8+ for build automation and dependency management (Gradle Wrapper included in repo)
- **Consistent versioning:** Both projects use semantic versioning (MAJOR.MINOR.PATCH)

**Database:**
- **Primary Database:** PostgreSQL 15+ for relational data storage (transactions, users, categories)
- **Schema Management:** Flyway for database migrations with versioned SQL scripts in `backend/src/main/resources/db/migration`
- **Caching (Future):** Redis consideration for session management and frequently accessed data (not required for MVP)

**Security:**
- **Authentication:** JWT-based stateless authentication with refresh token support
- **Password Storage:** bcrypt hashing with minimum work factor of 10
- **CORS Configuration:** Backend configured to allow frontend origin only
- **API Security:** All endpoints except login/register require valid JWT token
- **Input Validation:** Bean Validation (JSR-303) annotations on all request DTOs
- **SQL Injection Prevention:** Parameterized queries via JPA/Hibernate (no raw SQL concatenation)

**AI/ML for Categorization:**
- **MVP Approach:** Rule-based classification using keyword matching and regex patterns (e.g., "Starbucks" → Food, "Shell" → Transport) with user feedback storage
- **Learning Mechanism:** Store user corrections in database to refine rules for that specific user over time
- **Future Enhancement:** Replace with actual ML model (scikit-learn, TensorFlow, or cloud ML API) in Phase 2

**Deployment and Infrastructure:**
- **Containerization:** Docker images for both frontend (nginx serving static files) and backend (Java application)
- **Local Development:** docker-compose.yml orchestrates frontend, backend, and PostgreSQL for one-command startup
- **Cloud Hosting:** AWS, GCP, or Azure (decision deferred until deployment phase); target free-tier usage where possible
- **CI/CD Pipeline:** GitHub Actions for automated testing, building, and deployment to staging/production environments
- **Environment Configuration:** Separate configuration files for dev, staging, prod environments (application-dev.yml, application-prod.yml, etc.)

**Code Quality and Standards:**
- **Backend:** Follow Spring Boot best practices, use Lombok for boilerplate reduction, SonarQube for static analysis
- **Frontend:** Follow Angular style guide (angular.io/guide/styleguide), use ESLint and Prettier for code formatting
- **Documentation:** Comprehensive README files in both frontend/ and backend/ directories, Swagger/OpenAPI spec for API documentation
- **BMAD Compliance:** All architectural decisions documented in docs/architecture/, coding standards in docs/architecture/coding-standards.md

**Logging and Monitoring:**
- **Backend Logging:** SLF4J with Logback, structured logging in JSON format for production
- **Frontend Error Handling:** Angular ErrorHandler implementation with user-friendly messages and error reporting (console logging for MVP, Sentry integration in Phase 2)
- **Health Checks:** Spring Boot Actuator endpoints for application health and metrics

**Development Workflow:**
- **Version Control:** Git with feature branch workflow (feature/*, bugfix/*)
- **Pull Request Process:** All changes via PR with required code review before merge
- **Branch Protection:** main branch protected, requires passing CI checks
- **Commit Messages:** Conventional Commits format for clear changelog generation
