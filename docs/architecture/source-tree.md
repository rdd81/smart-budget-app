# Project Source Tree

This document defines the **complete directory structure** for the Smart Budget App monorepo. This structure must be followed by all development.

## Repository Structure Overview

The Smart Budget App uses a **monorepo** structure housing both the Angular frontend and Spring Boot backend in a single repository. This approach provides:
- Simplified dependency management and coordinated versioning
- Easier code sharing for API contracts and utilities
- Streamlined CI/CD with a single pipeline
- Single clone and workspace for developers

## Complete Directory Structure

```
smart-budget-app/
├── .github/                          # GitHub-specific configuration
│   └── workflows/                    # CI/CD pipeline definitions
│       ├── ci.yml                    # Continuous integration (build, test, lint)
│       ├── deploy-staging.yml        # Deploy to staging environment
│       └── deploy-production.yml     # Deploy to production environment
│
├── .vscode/                          # VS Code workspace settings
│   ├── settings.json                 # Editor settings and formatting
│   ├── extensions.json               # Recommended extensions
│   └── launch.json                   # Debug configurations
│
├── frontend/                         # Angular 17+ application
│   ├── src/                          # Application source code
│   │   ├── app/                      # Application module
│   │   │   ├── core/                 # Core singleton services and guards
│   │   │   │   ├── guards/           # Route guards (auth-guard.ts)
│   │   │   │   ├── interceptors/     # HTTP interceptors (auth-interceptor.ts, error-interceptor.ts)
│   │   │   │   ├── services/         # Singleton services (auth.service.ts, api.service.ts)
│   │   │   │   └── core.module.ts    # Core module (imported once in AppModule)
│   │   │   ├── shared/               # Shared components, directives, pipes
│   │   │   │   ├── components/       # Reusable components (button, card, input, etc.)
│   │   │   │   ├── directives/       # Custom directives
│   │   │   │   ├── pipes/            # Custom pipes (currency-format, date-format)
│   │   │   │   ├── models/           # Shared TypeScript interfaces and types
│   │   │   │   └── shared.module.ts  # Shared module (imported by feature modules)
│   │   │   ├── features/             # Feature modules (lazy-loaded)
│   │   │   │   ├── auth/             # Authentication feature
│   │   │   │   │   ├── components/   # Login, register components
│   │   │   │   │   │   ├── login/
│   │   │   │   │   │   │   ├── login.component.ts
│   │   │   │   │   │   │   ├── login.component.html
│   │   │   │   │   │   │   ├── login.component.scss
│   │   │   │   │   │   │   └── login.component.spec.ts
│   │   │   │   │   │   └── register/
│   │   │   │   │   ├── services/     # Auth-specific services
│   │   │   │   │   ├── auth-routing.module.ts
│   │   │   │   │   └── auth.module.ts
│   │   │   │   ├── dashboard/        # Dashboard feature
│   │   │   │   │   ├── components/   # Dashboard, summary cards, charts
│   │   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── summary-cards/
│   │   │   │   │   │   ├── category-breakdown/
│   │   │   │   │   │   ├── expense-pie-chart/
│   │   │   │   │   │   └── trends-chart/
│   │   │   │   │   ├── services/     # Dashboard-specific services (analytics.service.ts)
│   │   │   │   │   ├── dashboard-routing.module.ts
│   │   │   │   │   └── dashboard.module.ts
│   │   │   │   ├── transactions/     # Transaction management feature
│   │   │   │   │   ├── components/   # Transaction list, add/edit forms
│   │   │   │   │   │   ├── transaction-list/
│   │   │   │   │   │   ├── transaction-form/
│   │   │   │   │   │   └── transaction-filters/
│   │   │   │   │   ├── services/     # Transaction-specific services (transaction.service.ts)
│   │   │   │   │   ├── models/       # Transaction interfaces
│   │   │   │   │   ├── transactions-routing.module.ts
│   │   │   │   │   └── transactions.module.ts
│   │   │   │   └── profile/          # User profile feature
│   │   │   │       ├── components/   # Profile view, settings
│   │   │   │       ├── services/
│   │   │   │       ├── profile-routing.module.ts
│   │   │   │       └── profile.module.ts
│   │   │   ├── app.component.ts      # Root component
│   │   │   ├── app.component.html    # Root template
│   │   │   ├── app.component.scss    # Root styles
│   │   │   ├── app.component.spec.ts # Root component tests
│   │   │   ├── app.config.ts         # Application configuration
│   │   │   ├── app.routes.ts         # Route configuration
│   │   │   └── app.module.ts         # Root module
│   │   ├── assets/                   # Static assets
│   │   │   ├── images/               # Images and icons
│   │   │   ├── fonts/                # Custom fonts
│   │   │   └── i18n/                 # Internationalization files (future)
│   │   ├── environments/             # Environment-specific configuration
│   │   │   ├── environment.ts        # Development environment
│   │   │   ├── environment.staging.ts # Staging environment
│   │   │   └── environment.prod.ts   # Production environment
│   │   ├── styles/                   # Global styles
│   │   │   ├── _variables.scss       # SCSS variables (colors, spacing)
│   │   │   ├── _mixins.scss          # Reusable SCSS mixins
│   │   │   └── styles.scss           # Main stylesheet (imports Tailwind, Material theme)
│   │   ├── index.html                # HTML entry point
│   │   ├── main.ts                   # Application bootstrap
│   │   └── polyfills.ts              # Browser polyfills
│   ├── public/                       # Static files (copied to dist)
│   │   └── favicon.ico               # Favicon
│   ├── tests/                        # Test configuration and utilities
│   │   ├── setup.ts                  # Test setup file
│   │   └── mocks/                    # Mock data for testing
│   ├── .eslintrc.json                # ESLint configuration
│   ├── .prettierrc                   # Prettier configuration
│   ├── angular.json                  # Angular CLI configuration
│   ├── karma.conf.js                 # Karma test runner configuration
│   ├── tsconfig.json                 # TypeScript configuration (base)
│   ├── tsconfig.app.json             # TypeScript config for application
│   ├── tsconfig.spec.json            # TypeScript config for tests
│   ├── tailwind.config.js            # Tailwind CSS configuration
│   ├── package.json                  # Frontend dependencies
│   ├── package-lock.json             # Locked dependency versions
│   └── README.md                     # Frontend-specific documentation
│
├── backend/                          # Spring Boot Java 21 application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/smartbudget/
│   │   │   │       ├── SmartBudgetApplication.java  # Spring Boot main class
│   │   │   │       ├── config/                      # Configuration classes
│   │   │   │       │   ├── SecurityConfig.java      # Spring Security config (JWT, CORS)
│   │   │   │       │   ├── OpenApiConfig.java       # Swagger/OpenAPI config
│   │   │   │       │   └── WebConfig.java           # Web MVC config
│   │   │   │       ├── controller/                  # REST API controllers
│   │   │   │       │   ├── AuthController.java      # /api/auth/* (login, register)
│   │   │   │       │   ├── TransactionController.java # /api/transactions/* (CRUD)
│   │   │   │       │   ├── CategoryController.java  # /api/categories/* (list)
│   │   │   │       │   ├── AnalyticsController.java # /api/analytics/* (summaries, trends)
│   │   │   │       │   └── UserController.java      # /api/users/* (profile, settings)
│   │   │   │       ├── service/                     # Business logic layer
│   │   │   │       │   ├── AuthService.java         # Authentication logic
│   │   │   │       │   ├── TransactionService.java  # Transaction business logic
│   │   │   │       │   ├── CategoryService.java     # Category management
│   │   │   │       │   ├── AnalyticsService.java    # Data aggregation and analytics
│   │   │   │       │   ├── CategorizationService.java # AI categorization logic
│   │   │   │       │   └── UserService.java         # User management
│   │   │   │       ├── repository/                  # Data access layer (Spring Data JPA)
│   │   │   │       │   ├── UserRepository.java      # User CRUD operations
│   │   │   │       │   ├── TransactionRepository.java # Transaction CRUD + queries
│   │   │   │       │   ├── CategoryRepository.java  # Category CRUD
│   │   │   │       │   ├── CategorizationRuleRepository.java
│   │   │   │       │   └── CategorizationFeedbackRepository.java
│   │   │   │       ├── model/                       # JPA entity models
│   │   │   │       │   ├── User.java                # User entity (@Entity, @Table)
│   │   │   │       │   ├── Transaction.java         # Transaction entity
│   │   │   │       │   ├── Category.java            # Category entity
│   │   │   │       │   ├── CategorizationRule.java  # AI rule entity
│   │   │   │       │   └── CategorizationFeedback.java # User feedback entity
│   │   │   │       ├── dto/                         # Data Transfer Objects
│   │   │   │       │   ├── request/                 # Request DTOs
│   │   │   │       │   │   ├── LoginRequest.java
│   │   │   │       │   │   ├── RegisterRequest.java
│   │   │   │       │   │   ├── TransactionRequest.java
│   │   │   │       │   │   └── CategorySuggestionRequest.java
│   │   │   │       │   └── response/                # Response DTOs
│   │   │   │       │       ├── AuthResponse.java
│   │   │   │       │       ├── TransactionResponse.java
│   │   │   │       │       ├── SummaryResponse.java
│   │   │   │       │       ├── CategoryBreakdownResponse.java
│   │   │   │       │       └── TrendDataPoint.java
│   │   │   │       ├── security/                    # Security components
│   │   │   │       │   ├── JwtTokenProvider.java    # JWT token generation/validation
│   │   │   │       │   ├── JwtAuthenticationFilter.java # JWT filter
│   │   │   │       │   └── UserDetailsServiceImpl.java # Spring Security user details
│   │   │   │       ├── exception/                   # Custom exceptions
│   │   │   │       │   ├── ResourceNotFoundException.java
│   │   │   │       │   ├── UnauthorizedException.java
│   │   │   │       │   ├── ValidationException.java
│   │   │   │       │   └── GlobalExceptionHandler.java # @ControllerAdvice
│   │   │   │       └── util/                        # Utility classes
│   │   │   │           ├── DateUtil.java
│   │   │   │           └── ValidationUtil.java
│   │   │   └── resources/
│   │   │       ├── application.yml                  # Main configuration
│   │   │       ├── application-dev.yml              # Development config
│   │   │       ├── application-staging.yml          # Staging config
│   │   │       ├── application-prod.yml             # Production config
│   │   │       ├── db/migration/                    # Flyway migrations
│   │   │       │   ├── V1__initial_schema.sql       # Initial tables
│   │   │       │   ├── V2__seed_categories.sql      # Seed category data
│   │   │       │   └── V3__categorization_tables.sql # AI categorization tables
│   │   │       └── static/                          # Static resources (if any)
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/smartbudget/
│   │       │       ├── controller/                  # Controller tests (MockMvc)
│   │       │       ├── service/                     # Service unit tests (Mockito)
│   │       │       ├── repository/                  # Repository integration tests (TestContainers)
│   │       │       └── integration/                 # Full integration tests
│   │       └── resources/
│   │           ├── application-test.yml             # Test configuration
│   │           └── test-data/                       # Test data SQL scripts
│   ├── gradle/                                      # Gradle wrapper
│   │   └── wrapper/
│   ├── build.gradle                                 # Gradle build configuration
│   ├── settings.gradle                              # Gradle settings
│   ├── gradlew                                      # Gradle wrapper script (Unix)
│   ├── gradlew.bat                                  # Gradle wrapper script (Windows)
│   └── README.md                                    # Backend-specific documentation
│
├── docs/                             # BMAD documentation
│   ├── prd/                          # Product Requirements (sharded)
│   │   ├── index.md
│   │   ├── goals-and-background-context.md
│   │   ├── requirements.md
│   │   ├── epic-1-foundation-core-infrastructure.md
│   │   ├── epic-2-transaction-management.md
│   │   ├── epic-3-dashboard-visual-analytics.md
│   │   └── epic-4-ai-powered-categorization.md
│   ├── architecture/                 # Architecture documentation
│   │   ├── tech-stack.md             # Technology stack (THIS FILE ALWAYS LOADED)
│   │   ├── source-tree.md            # Project structure (THIS FILE)
│   │   ├── coding-standards.md       # Coding standards (ALWAYS LOADED)
│   │   ├── api-spec.md               # OpenAPI specification
│   │   └── database-schema.md        # Database ERD and migrations
│   ├── brief.md                      # Project brief
│   ├── prd.md                        # Full PRD (monolithic, if not sharded)
│   ├── front-end-spec.md             # UI/UX specification
│   └── architecture.md               # Main architecture document
│
├── scripts/                          # Build and deployment scripts
│   ├── setup-dev.sh                  # Local development setup
│   ├── seed-database.sh              # Seed test data
│   ├── run-tests.sh                  # Run all tests
│   └── deploy.sh                     # Deployment script
│
├── .env.example                      # Environment variable template
├── .gitignore                        # Git ignore patterns
├── docker-compose.yml                # Docker Compose for local dev
├── Dockerfile.frontend               # Frontend Docker image
├── Dockerfile.backend                # Backend Docker image
├── README.md                         # Project overview and setup
└── LICENSE                           # Project license

```

## Module Organization Principles

### Frontend (Angular)

**Core Module (`src/app/core/`):**
- **Purpose:** Singleton services and app-wide singletons
- **Imported:** Once in AppModule only
- **Contains:** AuthService, ApiService, HTTP interceptors, route guards
- **Rule:** Never import CoreModule in feature modules

**Shared Module (`src/app/shared/`):**
- **Purpose:** Reusable components, directives, pipes
- **Imported:** By feature modules that need shared components
- **Contains:** Button, Card, Input components, custom pipes, shared interfaces
- **Rule:** Should NOT have providers (use Core for services)

**Feature Modules (`src/app/features/`):**
- **Purpose:** Encapsulated features with their own routing
- **Lazy-loaded:** Each feature module loaded on-demand via routing
- **Contains:** Feature-specific components, services, and models
- **Rule:** Features should be independent and not import each other directly

### Backend (Spring Boot)

**Layered Architecture:**

1. **Controller Layer** (`controller/`)
   - REST API endpoints
   - Request/response DTOs
   - Input validation (@Valid)
   - Delegates to Service layer

2. **Service Layer** (`service/`)
   - Business logic
   - Transaction management (@Transactional)
   - Orchestrates repositories
   - Throws domain exceptions

3. **Repository Layer** (`repository/`)
   - Data access via Spring Data JPA
   - Custom queries with @Query
   - No business logic

4. **Model Layer** (`model/`)
   - JPA entities (@Entity)
   - Database mappings
   - Relationships (@OneToMany, @ManyToOne)

## File Naming Conventions

### Frontend (Angular)

| Type | Pattern | Example |
|------|---------|---------|
| Components | `{name}.component.ts` | `login.component.ts` |
| Services | `{name}.service.ts` | `auth.service.ts` |
| Guards | `{name}.guard.ts` | `auth.guard.ts` |
| Interceptors | `{name}.interceptor.ts` | `auth.interceptor.ts` |
| Pipes | `{name}.pipe.ts` | `currency-format.pipe.ts` |
| Directives | `{name}.directive.ts` | `tooltip.directive.ts` |
| Modules | `{name}.module.ts` | `dashboard.module.ts` |
| Interfaces | `{name}.interface.ts` or `{name}.model.ts` | `transaction.interface.ts` |

### Backend (Spring Boot)

| Type | Pattern | Example |
|------|---------|---------|
| Controllers | `{Name}Controller.java` | `TransactionController.java` |
| Services | `{Name}Service.java` | `TransactionService.java` |
| Repositories | `{Name}Repository.java` | `TransactionRepository.java` |
| Entities | `{Name}.java` | `Transaction.java` |
| DTOs (Request) | `{Name}Request.java` | `TransactionRequest.java` |
| DTOs (Response) | `{Name}Response.java` | `TransactionResponse.java` |
| Config | `{Name}Config.java` | `SecurityConfig.java` |
| Exceptions | `{Name}Exception.java` | `ResourceNotFoundException.java` |

## Import Rules

### Frontend

```typescript
// Absolute imports using TypeScript paths (configured in tsconfig.json)
import { AuthService } from '@core/services/auth.service';
import { ButtonComponent } from '@shared/components/button/button.component';
import { Transaction } from '@shared/models/transaction.interface';
```

**Path Aliases (tsconfig.json):**
```json
{
  "compilerOptions": {
    "paths": {
      "@core/*": ["src/app/core/*"],
      "@shared/*": ["src/app/shared/*"],
      "@features/*": ["src/app/features/*"],
      "@environments/*": ["src/environments/*"]
    }
  }
}
```

### Backend

```java
// Package imports follow standard Java conventions
import com.smartbudget.service.TransactionService;
import com.smartbudget.model.Transaction;
import com.smartbudget.dto.request.TransactionRequest;
```

## Build Output Locations

### Frontend
- **Development:** `frontend/dist/` (not committed)
- **Production:** `frontend/dist/browser/` (static files for nginx)

### Backend
- **Development:** `backend/build/` (not committed)
- **JAR file:** `backend/build/libs/smart-budget-app-{version}.jar`

## Critical Files for Dev Agents

These files are **automatically loaded** by dev agents (configured in `.bmad-core/core-config.yaml`):

1. **`docs/architecture/tech-stack.md`** - Technology decisions
2. **`docs/architecture/source-tree.md`** - This file (project structure)
3. **`docs/architecture/coding-standards.md`** - Coding rules and conventions

## Where to Find Key Functionality

| Functionality | Frontend Location | Backend Location |
|---------------|-------------------|------------------|
| Authentication | `features/auth/` | `controller/AuthController.java`, `service/AuthService.java` |
| Transaction Management | `features/transactions/` | `controller/TransactionController.java`, `service/TransactionService.java` |
| Dashboard & Analytics | `features/dashboard/` | `controller/AnalyticsController.java`, `service/AnalyticsService.java` |
| AI Categorization | `features/transactions/services/` | `service/CategorizationService.java` |
| API Client | `core/services/api.service.ts` | N/A |
| HTTP Interceptors | `core/interceptors/` | N/A |
| Route Guards | `core/guards/auth.guard.ts` | N/A |
| JWT Token Handling | `core/interceptors/auth.interceptor.ts` | `security/JwtTokenProvider.java`, `security/JwtAuthenticationFilter.java` |
| Error Handling | `core/interceptors/error.interceptor.ts` | `exception/GlobalExceptionHandler.java` |
| Database Migrations | N/A | `resources/db/migration/V*.sql` |
| Configuration | `environments/*.ts` | `resources/application*.yml` |

---

**Last Updated:** 2025-11-11
**Version:** 1.0
**Owner:** Architect (Winston)
**Status:** Approved for MVP Development
