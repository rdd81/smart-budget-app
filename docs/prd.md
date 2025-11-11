# Smart Budget App Product Requirements Document (PRD)

## Goals and Background Context

### Goals

- Enable users to effortlessly track income and expense transactions with minimal friction
- Provide clear visibility into spending patterns through visual charts and summaries
- Reduce manual categorization burden through AI-powered transaction classification
- Help users understand their financial health and make data-informed budget decisions
- Deliver actionable insights that drive behavior change and improved financial outcomes
- Establish a solid technical foundation using BMAD methodology for rapid, sustainable development
- Validate product-market fit with 20+ beta users actively using the app for 30+ days
- Achieve 75%+ AI categorization accuracy to demonstrate "smart" value proposition

### Background Context

Smart Budget App addresses a critical gap in personal finance management for budget-conscious individuals who struggle with unclear spending patterns and cumbersome tracking methods. Current solutions either require significant manual effort (spreadsheets), lack analytical depth (basic banking apps), or overwhelm users with complexity (enterprise financial software). Our target users—primarily young professionals aged 22-35—want to understand where their money goes without becoming accountants.

The application combines intuitive transaction management with AI-powered categorization and visual analytics to transform raw financial data into meaningful insights. By focusing on web-first delivery with a mobile-responsive design, we ensure accessibility across all devices without installation barriers. The MVP prioritizes core transaction tracking, categorization, and visualization features while deliberately excluding banking integration, multi-user sharing, and advanced AI recommendations to maintain focus and accelerate time-to-market within the 8-12 week development timeline.

### Change Log

| Date | Version | Description | Author |
|------|---------|-------------|--------|
| 2025-11-11 | 1.0 | Initial PRD created from Project Brief | John (PM Agent) |

## Requirements

### Functional

**FR1:** Users must be able to create an account using email and password with secure authentication

**FR2:** Users must be able to log in and log out of their account securely

**FR3:** Users must be able to add a new transaction with the following fields: amount (numeric), date, description (text), transaction type (income or expense), and category

**FR4:** Users must be able to edit existing transactions to correct errors or update information

**FR5:** Users must be able to delete transactions they no longer want to track

**FR6:** Users must be able to view a chronological list of all their transactions with sorting capabilities (by date, amount, category)

**FR7:** The system must provide a predefined set of transaction categories including: Salary, Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Savings, Investments, and Other

**FR8:** Users must be able to assign a category to each transaction during entry or edit

**FR9:** Users must be able to change the category assignment for any transaction

**FR10:** The system must display a dashboard summary showing total income, total expenses, and net balance for a selected time period (current month, last month, last 3 months, last 6 months, current year)

**FR11:** The dashboard must display a breakdown of expenses by category showing the amount and percentage of total spending for each category

**FR12:** Users must be able to view a pie chart visualizing expense distribution across categories

**FR13:** Users must be able to view a bar chart or line chart showing spending trends over time (monthly comparison)

**FR14:** The system must provide AI-powered category suggestions based on transaction description and amount patterns

**FR15:** The AI categorization system must learn from user corrections to improve accuracy over time for that specific user

**FR16:** Users must be able to accept or reject AI category suggestions

**FR17:** The system must support filtering transactions by date range, category, and transaction type

**FR18:** The system must calculate and display month-over-month spending comparisons

**FR19:** Users must be able to view their profile information and update basic account settings (email, password)

**FR20:** The system must persist all user data securely and ensure data is only accessible to the authenticated user who owns it

### Non Functional

**NFR1:** Page load time must be under 2 seconds on a standard broadband connection

**NFR2:** User interaction response time (clicks, form submissions) must be under 200ms

**NFR3:** The system must support up to 10,000 transactions per user without performance degradation

**NFR4:** Chart rendering must complete in under 1 second for typical monthly data sets (up to 100 transactions)

**NFR5:** The application must be responsive and functional on screen sizes from 320px (mobile) to 2560px+ (large desktop)

**NFR6:** The application must be compatible with modern browsers: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+

**NFR7:** All passwords must be hashed using bcrypt before storage with a minimum work factor of 10

**NFR8:** All API communications must occur over HTTPS with TLS 1.2 or higher

**NFR9:** Financial data must be encrypted at rest in the database

**NFR10:** The system must implement JWT-based authentication with appropriate token expiration (24 hours for access tokens)

**NFR11:** The frontend Angular application must follow Angular style guide and use TypeScript strict mode

**NFR12:** The backend Spring Boot application must follow layered architecture (Controller, Service, Repository) with clear separation of concerns

**NFR13:** All backend endpoints must include appropriate input validation and error handling

**NFR14:** Unit test coverage must reach a minimum of 70% for backend services

**NFR15:** The application must gracefully handle network errors and display user-friendly error messages

**NFR16:** The AI categorization system must achieve a minimum of 75% accuracy on common transaction types by MVP completion

**NFR17:** The application must be deployable via Docker containers to support consistent environments across development, staging, and production

**NFR18:** The system must implement proper logging for debugging and monitoring (using SLF4J with Logback for backend, Angular error handling for frontend)

**NFR19:** Database migrations must be managed using Flyway or Liquibase for version control and reproducibility

**NFR20:** The codebase must follow BMAD documentation standards with comprehensive README files, architecture documentation, and inline code comments

## User Interface Design Goals

### Overall UX Vision

Smart Budget App delivers a clean, modern, and approachable interface that makes financial management feel empowering rather than overwhelming. The design prioritizes clarity and efficiency—users should accomplish their goals (add transaction, view insights) with minimal clicks and cognitive load. Visual feedback is immediate, data is presented in digestible chunks, and the color palette uses calming, trustworthy tones (blues and greens) with purposeful accent colors for income (green) and expenses (red/orange). The overall aesthetic balances professional financial credibility with welcoming accessibility, avoiding both the sterile corporate feel of banking apps and the overly playful tone of some consumer apps.

### Key Interaction Paradigms

- **Transaction Entry:** Quick-add form accessible from any screen via persistent action button, with inline AI category suggestions appearing as user types description
- **Dashboard-First Navigation:** Landing page after login is always the dashboard showing current month summary, with clearly visible navigation to transaction list and other views
- **Time Period Selection:** Consistent date range picker component (dropdown or calendar) appears at top of all data views, with preset options (This Month, Last Month, Last 3 Months, etc.) for convenience
- **Chart Interactivity:** Hover states on chart segments reveal detailed breakdowns; clicking a category in pie chart filters transaction list to show only that category
- **Responsive Adaptation:** Mobile view prioritizes vertical scrolling with stacked charts and collapsible sections; desktop view leverages horizontal space with side-by-side chart comparisons
- **Inline Editing:** Transaction list supports direct inline editing (click to edit) rather than navigating to separate edit screens, reducing friction for corrections
- **Immediate Feedback:** All actions (add, edit, delete) update UI optimistically with loading states, followed by confirmation messages that auto-dismiss

### Core Screens and Views

- **Login/Registration Screen:** Simple, centered form with email/password fields and clear call-to-action buttons
- **Dashboard (Main Landing Page):** Displays current month summary cards (Income, Expenses, Balance), category breakdown table, and primary charts (pie chart for categories, line/bar chart for trends)
- **Transaction List View:** Paginated or infinite-scroll table showing all transactions with columns for date, description, amount, category, type (income/expense), and action buttons (edit, delete)
- **Add/Edit Transaction Form:** Modal or slide-in panel with fields for amount, date picker, description, category selector (with AI suggestions), and income/expense toggle
- **Analytics/Charts View:** Dedicated screen for deeper chart exploration with multiple visualization options and time period comparisons
- **Profile/Settings Page:** User account information, password change, basic preferences (future: export data, delete account)

### Accessibility: WCAG AA

The application will target WCAG 2.1 Level AA compliance to ensure accessibility for users with disabilities. Key considerations include:

- **Color Contrast:** All text and interactive elements maintain a minimum 4.5:1 contrast ratio (3:1 for large text)
- **Keyboard Navigation:** All functionality accessible via keyboard with visible focus indicators and logical tab order
- **Screen Reader Support:** Proper semantic HTML, ARIA labels for interactive components, and meaningful alt text for data visualizations
- **Form Labels:** All form inputs have associated labels and clear error messages
- **Responsive Text:** Text can be resized up to 200% without loss of functionality
- **Chart Accessibility:** Data tables or text alternatives provided for all charts to support screen readers

### Branding

The Smart Budget App uses a modern, minimalist design language with the following characteristics:

- **Color Palette:**
  - Primary: Professional blue (#2563EB) for primary actions and navigation
  - Success/Income: Green (#10B981) for positive balances and income
  - Warning/Expense: Orange-red (#EF4444) for expenses and alerts
  - Neutral: Gray scale (#F9FAFB to #111827) for backgrounds and text
  - Accent: Teal (#14B8A6) for highlights and secondary actions

- **Typography:**
  - Modern sans-serif font stack (Inter, Roboto, or system fonts)
  - Clear hierarchy with consistent sizing (H1: 32px, H2: 24px, Body: 16px)
  - Readable line heights (1.5-1.6) for optimal legibility

- **Visual Style:**
  - Rounded corners (4-8px border radius) for friendly approachability
  - Subtle shadows for depth and card separation
  - Ample white space to reduce visual clutter
  - Icon usage for common actions (add, edit, delete, filter) using consistent icon library (Material Icons or Heroicons)

- **Logo/Identity:** Clean, simple logo combining a dollar sign or piggy bank icon with modern typography (to be designed)

### Target Device and Platforms: Web Responsive

**Primary Target:** Web application optimized for desktop browsers (1920x1080 and 1366x768 most common resolutions)

**Secondary Target:** Mobile-responsive web (iOS Safari 12+, Chrome Mobile, Firefox Mobile) supporting screen sizes from 320px (iPhone SE) to tablet sizes (1024px)

**Responsive Breakpoints:**
- Mobile: 320px - 639px (single column, stacked layouts, hamburger menu)
- Tablet: 640px - 1023px (two-column where appropriate, condensed charts)
- Desktop: 1024px+ (full multi-column layouts, side-by-side charts, expanded navigation)

**No Native Apps in MVP:** The application will NOT include native iOS or Android applications in the MVP phase. All mobile access is through mobile-responsive web browsers.

## Technical Assumptions

### Repository Structure: Monorepo

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

### Service Architecture

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

### Testing Requirements

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

### Additional Technical Assumptions and Requests

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

## Epic List

The Smart Budget App MVP will be delivered through 4 sequential epics, each building upon the previous to deliver complete, deployable functionality:

**Epic 1: Foundation & Core Infrastructure**
Establish project structure, development environment, CI/CD pipeline, database setup, and basic authentication to create a solid foundation for all subsequent features while delivering a functional login/registration system.

**Epic 2: Transaction Management**
Implement complete transaction lifecycle (create, read, update, delete) with category assignment, enabling users to record and manage their financial data as the core functionality of the application.

**Epic 3: Dashboard & Visual Analytics**
Build the summary dashboard with aggregated financial data and interactive charts, transforming raw transaction data into actionable visual insights that help users understand their spending patterns.

**Epic 4: AI-Powered Categorization**
Add intelligent transaction categorization with suggestion engine and learning from user feedback, reducing manual effort and demonstrating the "smart" value proposition of the application.

## Epic 1: Foundation & Core Infrastructure

**Epic Goal:**
Establish the complete technical foundation for the Smart Budget App including project structure, development environment setup, containerization, CI/CD pipeline, database configuration, and user authentication. This epic delivers a fully deployable application with working registration and login functionality, setting the stage for all business features while validating the development workflow and deployment process.

### Story 1.1: Project Initialization and Repository Structure

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

### Story 1.2: Docker Containerization and Local Development Environment

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

### Story 1.3: Database Schema and Migration Framework

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

### Story 1.4: CI/CD Pipeline Setup

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

### Story 1.5: User Registration Backend API

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

### Story 1.6: User Login and JWT Authentication

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

### Story 1.7: Frontend Authentication UI and State Management

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

### Story 1.8: Deployment Configuration and Documentation

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

## Epic 2: Transaction Management

**Epic Goal:**
Implement the complete transaction management system enabling users to create, view, edit, and delete financial transactions with category assignment. This epic delivers the core data entry and management functionality that forms the foundation of the budgeting experience, allowing users to build their financial dataset for analysis.

### Story 2.1: Backend Transaction CRUD API

As a developer,
I want RESTful API endpoints for transaction management,
so that the frontend can perform all transaction operations securely with proper validation and authorization.

**Acceptance Criteria:**

1. GET /api/transactions endpoint returns paginated list of transactions for authenticated user (filtered by user_id from JWT)
2. GET /api/transactions/{id} endpoint returns single transaction if owned by authenticated user, 404 if not found, 403 if owned by different user
3. POST /api/transactions endpoint creates new transaction with request body: amount, transactionDate, description, categoryId, transactionType (INCOME/EXPENSE)
4. PUT /api/transactions/{id} endpoint updates existing transaction with same fields as POST, validates ownership
5. DELETE /api/transactions/{id} endpoint soft-deletes or hard-deletes transaction, validates ownership
6. TransactionRequest DTO created with validation: amount required and positive, transactionDate required and not future, description max 255 characters, categoryId must be valid existing category, transactionType required enum
7. TransactionResponse DTO includes: id, amount, transactionDate, description, category (nested object with id and name), transactionType, createdAt, updatedAt
8. Service layer (TransactionService) implements business logic with proper authorization checks (user can only access own transactions)
9. Repository queries filter by user_id automatically to prevent data leakage
10. GET /api/transactions supports query parameters: page, size, sortBy (date, amount), sortDirection (asc, desc)
11. All endpoints return appropriate HTTP status codes and error messages
12. Unit tests cover TransactionService logic with >70% coverage
13. Integration tests validate end-to-end flows including authorization checks
14. API documented with OpenAPI/Swagger annotations

### Story 2.2: Category Management API

As a developer,
I want API endpoints to retrieve available categories,
so that the frontend can display category options and validate category assignments.

**Acceptance Criteria:**

1. GET /api/categories endpoint returns list of all available categories (no pagination needed, small dataset)
2. CategoryResponse DTO includes: id, name, type (INCOME/EXPENSE), description
3. Categories are system-wide (not user-specific) - all users see same category list
4. Response includes categories seeded during database migration: Salary, Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Savings, Investments, Other
5. Categories separated by type: income categories (Salary, Savings, Investments, Other) and expense categories (Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Other)
6. CategoryService and CategoryRepository created following layered architecture
7. Endpoint does not require authentication (categories are public reference data) - ALTERNATIVE: require authentication for consistency
8. Response cached appropriately (categories rarely change)
9. Unit and integration tests validate category retrieval
10. API documented with OpenAPI/Swagger annotations

### Story 2.3: Transaction List View Component

As a user,
I want to view a list of all my transactions in a clear, organized table,
so that I can see my complete financial history at a glance and quickly find specific transactions.

**Acceptance Criteria:**

1. TransactionListComponent created displaying transactions in a responsive table/card layout
2. Table columns show: Date, Description, Category, Amount, Type (Income/Expense), Actions (Edit/Delete icons)
3. Amount displayed with currency formatting (e.g., $1,234.56) with color coding (green for income, red for expense)
4. Date formatted in user-friendly format (e.g., "Jan 15, 2025" or "01/15/2025")
5. Transactions sorted by date descending (most recent first) by default
6. Column headers clickable for sorting (toggle asc/desc) - at minimum sort by Date and Amount
7. Empty state displayed when no transactions exist with friendly message and call-to-action to add first transaction
8. Loading state displayed while fetching data from API
9. Error state displayed if API call fails with retry option
10. Pagination controls displayed if more than 20 transactions (page size configurable)
11. Mobile-responsive: table converts to card layout on screens < 640px
12. TransactionService (Angular) created with methods: getTransactions(), getTransaction(id), deleteTransaction(id)
13. Component tests validate rendering and user interactions

### Story 2.4: Add Transaction Form

As a user,
I want to add a new transaction with all relevant details,
so that I can record my income and expenses accurately and build my financial dataset.

**Acceptance Criteria:**

1. AddTransactionComponent created with reactive form or template-driven form
2. Form fields: Amount (number input, required, positive validation), Date (date picker, required, not future), Description (text input, required, max 255 chars), Category (dropdown select, required), Transaction Type (toggle or radio buttons for Income/Expense, required)
3. Category dropdown populated from GET /api/categories, filtered by transaction type (show income categories when Income selected, expense categories when Expense selected)
4. Date picker defaults to today's date
5. Amount input supports decimal values (e.g., 12.99) with appropriate step (0.01)
6. Form validation displays inline error messages below each field
7. Submit button disabled until form is valid
8. Submit calls POST /api/transactions via TransactionService
9. Success displays confirmation message (toast or snackbar) and redirects to transaction list or clears form for next entry
10. Failure displays error message from API
11. Loading state disables form during submission
12. Form accessible via floating action button (FAB) or prominent "Add Transaction" button on transaction list page
13. Modal or slide-in panel UI pattern for form (alternative: dedicated /transactions/new route)
14. Keyboard accessible with proper tab order and Enter to submit
15. Form includes Cancel button to close without saving

### Story 2.5: Edit Transaction Form

As a user,
I want to edit existing transactions to correct errors or update information,
so that my financial data remains accurate and up-to-date.

**Acceptance Criteria:**

1. Edit functionality triggered by clicking Edit icon/button in transaction list
2. EditTransactionComponent created (or AddTransactionComponent reused in edit mode)
3. Form pre-populated with existing transaction data fetched via GET /api/transactions/{id}
4. All fields editable with same validation rules as Add Transaction form
5. Submit calls PUT /api/transactions/{id} via TransactionService
6. Success displays confirmation message and updates transaction in list without full page reload (optimistic update or refetch)
7. Failure displays error message and reverts to previous state
8. Cancel button discards changes and closes form
9. Loading state displayed while fetching transaction data for edit
10. 404 handling if transaction no longer exists (deleted by another session)
11. Form accessible and keyboard navigable
12. Component tests validate edit flow and data population

### Story 2.6: Delete Transaction Functionality

As a user,
I want to delete transactions I no longer want to track,
so that I can remove errors or transactions that are no longer relevant to my budgeting.

**Acceptance Criteria:**

1. Delete functionality triggered by clicking Delete icon/button in transaction list
2. Confirmation dialog displayed before deletion: "Are you sure you want to delete this transaction? This action cannot be undone."
3. Confirmation dialog includes transaction description and amount for verification
4. Confirm button calls DELETE /api/transactions/{id} via TransactionService
5. Success displays confirmation message (toast/snackbar) and removes transaction from list
6. Failure displays error message (e.g., "Failed to delete transaction. Please try again.")
7. Transaction removed from UI optimistically or after API confirmation
8. Loading state displayed during deletion (disable delete button, show spinner)
9. Cancel button on confirmation dialog aborts deletion
10. Accessibility: confirmation dialog keyboard accessible (Tab to navigate, Enter to confirm, Esc to cancel)
11. Component tests validate deletion flow and confirmation dialog

### Story 2.7: Transaction Filtering and Search

As a user,
I want to filter transactions by date range, category, and transaction type,
so that I can quickly find specific transactions and analyze spending in particular areas.

**Acceptance Criteria:**

1. Filter controls added above transaction list: Date Range (from/to date pickers or preset options like "This Month", "Last Month"), Category (multi-select dropdown or filter chips), Transaction Type (All/Income/Expense toggle)
2. Applying filters updates transaction list without page reload
3. Filters applied as query parameters to GET /api/transactions endpoint
4. Backend supports filtering: dateFrom, dateTo, categoryId (can be multiple), transactionType parameters
5. Filter state persists during session (stored in component state or query parameters in URL)
6. "Clear Filters" button resets all filters to defaults
7. Filtered results count displayed (e.g., "Showing 15 of 120 transactions")
8. Empty state displayed when filters return no results: "No transactions match your filters. Try adjusting your criteria."
9. Date range validation: "to" date cannot be before "from" date
10. Filters work in combination (AND logic: date range AND category AND type)
11. Loading state displayed while fetching filtered results
12. Mobile-responsive: filters collapse into expandable panel or drawer on small screens

## Epic 3: Dashboard & Visual Analytics

**Epic Goal:**
Build a comprehensive dashboard that aggregates transaction data into meaningful summaries and visual charts, transforming raw financial information into actionable insights. Users will see their income vs. expenses, category breakdowns, spending trends, and month-over-month comparisons at a glance, helping them understand their financial patterns and make informed decisions.

### Story 3.1: Dashboard Summary Analytics Backend API

As a developer,
I want backend API endpoints that aggregate transaction data for dashboard summaries,
so that the frontend can display total income, total expenses, balance, and category breakdowns efficiently.

**Acceptance Criteria:**

1. GET /api/analytics/summary endpoint created accepting query parameters: startDate, endDate (defaults to current month if not provided)
2. Endpoint returns SummaryResponse DTO with: totalIncome (sum of all income transactions), totalExpenses (sum of all expense transactions), balance (totalIncome - totalExpenses), transactionCount, dateRange (start and end dates used)
3. GET /api/analytics/category-breakdown endpoint returns list of CategoryBreakdownResponse: categoryId, categoryName, totalAmount, transactionCount, percentage (of total expenses or income)
4. Category breakdown calculated separately for income and expense categories
5. Queries filter by authenticated user's transactions only (user_id from JWT)
6. Queries filter by date range (transaction_date between startDate and endDate)
7. Database queries optimized using aggregation functions (SUM, COUNT, GROUP BY) rather than loading all transactions into memory
8. AnalyticsService created implementing aggregation logic
9. Repository methods created using JPQL or Criteria API for complex aggregation queries
10. Endpoints handle edge cases: no transactions in date range returns zeros, empty category breakdowns
11. Unit tests validate calculation logic with mocked data
12. Integration tests validate end-to-end with real database queries
13. API documented with OpenAPI/Swagger annotations

### Story 3.2: Dashboard Summary Cards Component

As a user,
I want to see summary cards showing my total income, total expenses, and balance for the current month,
so that I immediately understand my financial position when I open the application.

**Acceptance Criteria:**

1. DashboardComponent created as main landing page after login (route: /dashboard, default route after authentication)
2. Three summary cards displayed prominently at top: Income (green accent), Expenses (red accent), Balance (blue or contextual: green if positive, red if negative)
3. Each card shows: Label (Income/Expenses/Balance), Amount (large, formatted as currency), Icon (relevant financial icon)
4. Data fetched from GET /api/analytics/summary with current month date range
5. Month selector component added (dropdown or date picker) allowing user to select different month/date range
6. Changing month triggers API call to fetch data for selected period
7. Loading state displayed while fetching summary data (skeleton cards or spinners)
8. Error state displayed if API call fails with retry button
9. Empty state displayed if no transactions exist in selected period: "No transactions found for this period. Add your first transaction to see insights."
10. Cards responsive: stack vertically on mobile (<640px), display side-by-side on tablet/desktop (≥640px)
11. Balance card changes color based on value: green if positive, red if negative, neutral if zero
12. Component tests validate data display and month selection behavior

### Story 3.3: Category Breakdown Table/List Component

As a user,
I want to see a breakdown of my spending by category,
so that I understand where my money is going and can identify areas for optimization.

**Acceptance Criteria:**

1. CategoryBreakdownComponent created and embedded in DashboardComponent
2. Data fetched from GET /api/analytics/category-breakdown for selected date range
3. Display format: Table or list showing Category Name, Amount, Transaction Count, Percentage of Total
4. Categories sorted by amount descending (highest spending first)
5. Percentage displayed with visual indicator (progress bar or colored background)
6. Separate breakdowns for income and expense categories (two tables or tabbed view)
7. Color coding: expense categories in red/orange spectrum, income categories in green spectrum
8. Clicking on a category navigates to transaction list filtered by that category
9. Empty state if no transactions: "No spending data available for this period."
10. Loading and error states handled gracefully
11. Responsive: table converts to card layout on mobile screens
12. Top 5 categories highlighted or displayed prominently with "View All" option to expand

### Story 3.4: Expense Category Pie Chart Visualization

As a user,
I want to see a pie chart visualizing my expense breakdown by category,
so that I can quickly grasp my spending distribution through an intuitive visual representation.

**Acceptance Criteria:**

1. ExpensePieChartComponent created using Chart.js or ngx-charts library
2. Pie chart displays expense categories with segment size proportional to spending amount
3. Each segment colored distinctly (use predefined color palette for consistency)
4. Chart legend displays category names with corresponding colors
5. Hover over segment displays tooltip: Category name, Amount, Percentage
6. Chart data fetched from GET /api/analytics/category-breakdown (expense categories only)
7. Chart updates when user changes selected month/date range
8. Empty state if no expense transactions: "Add expense transactions to see spending breakdown."
9. Chart responsive: adjusts size based on container, readable on mobile devices
10. Accessibility: Alternative text or data table provided for screen readers
11. Clicking on pie segment filters transaction list to show that category (optional enhancement)
12. Chart includes title: "Expense Breakdown by Category"

### Story 3.5: Spending Trends Line/Bar Chart

As a user,
I want to see a chart showing my spending trends over time,
so that I can identify patterns, seasonal variations, and track my progress toward budget goals.

**Acceptance Criteria:**

1. SpendingTrendsChartComponent created using Chart.js or ngx-charts
2. Chart type: Line chart or bar chart (user preference or configurable toggle)
3. X-axis: Time period (months for yearly view, weeks for quarterly view, days for monthly view)
4. Y-axis: Amount in currency
5. Two data series plotted: Total Income (green line/bars), Total Expenses (red line/bars)
6. Chart fetches data from new endpoint GET /api/analytics/trends with parameters: startDate, endDate, groupBy (DAY/WEEK/MONTH)
7. Backend endpoint aggregates transactions grouped by time period (daily, weekly, or monthly)
8. Default view shows last 6 months with monthly grouping
9. Hover over data point displays tooltip: Date/Period, Income amount, Expense amount, Net (income - expense)
10. Chart includes legend identifying income and expense series
11. Chart responsive and readable on all screen sizes
12. Empty state if no data: "Add transactions to see spending trends."
13. Loading state while fetching trend data
14. Chart title: "Income vs. Expenses Over Time"

### Story 3.6: Backend Trends Analytics API

As a developer,
I want a backend API endpoint that aggregates transaction data over time periods,
so that the frontend can display spending trends charts efficiently.

**Acceptance Criteria:**

1. GET /api/analytics/trends endpoint created with query parameters: startDate, endDate, groupBy (DAY/WEEK/MONTH)
2. Endpoint returns list of TrendDataPoint DTOs: period (date or date range), totalIncome, totalExpenses, net (income - expense), transactionCount
3. Aggregation groups transactions by requested time period using database date functions
4. Data sorted chronologically (oldest to newest)
5. Handles gaps in data: periods with no transactions return zero values (not omitted) for consistent chart display
6. Queries optimized for performance with proper indexing on transaction_date
7. AnalyticsService implements trend calculation logic
8. Unit tests validate grouping logic for different time periods
9. Integration tests validate database aggregation queries
10. API documented with OpenAPI/Swagger annotations

### Story 3.7: Month-over-Month Comparison Component

As a user,
I want to see how my spending this month compares to previous months,
so that I can track my progress and identify if I'm improving or regressing in my budgeting discipline.

**Acceptance Criteria:**

1. MonthComparisonComponent created and embedded in DashboardComponent
2. Displays comparison between current month and previous month: Income (current vs. previous, % change), Expenses (current vs. previous, % change), Balance (current vs. previous, % change)
3. Percentage change calculated as: ((current - previous) / previous) * 100
4. Positive change in income or balance displayed in green with up arrow icon
5. Negative change in expenses displayed in green with down arrow (spending less is good)
6. Negative change in income or balance displayed in red with down arrow
7. Handles division by zero: if previous month is zero, display "N/A" or "New data"
8. Data fetched using existing summary endpoint with two date ranges (current month, previous month)
9. Component shows comparison in compact card or table format
10. Empty state if insufficient data (only one month of transactions): "Add more transaction history to see comparisons."

### Story 3.8: Dashboard Date Range Selector and Filtering

As a user,
I want to change the date range for all dashboard analytics,
so that I can view my financial summary for different time periods (current month, last 3 months, custom range).

**Acceptance Criteria:**

1. Date range selector component added at top of dashboard with preset options: This Month, Last Month, Last 3 Months, Last 6 Months, This Year, Custom Range
2. Selecting preset option updates all dashboard components (summary cards, charts, breakdowns) with appropriate date range
3. Custom Range option opens date picker allowing user to select arbitrary start and end dates
4. Selected date range displayed prominently: "Showing data for: January 1 - January 31, 2025"
5. Date range persists during session (stored in component state or URL query parameters)
6. All dashboard components react to date range changes and fetch updated data
7. Loading states displayed during data refresh
8. Validation: End date cannot be before start date, no future dates allowed
9. Default view on dashboard load: Current month
10. Date range selector responsive: dropdown on mobile, button group on desktop

## Epic 4: AI-Powered Categorization

**Epic Goal:**
Implement intelligent transaction categorization that suggests appropriate categories based on transaction description and amount patterns, reducing manual effort for users and demonstrating the "smart" value proposition. The system learns from user corrections to improve accuracy over time on a per-user basis, progressively requiring less manual intervention.

### Story 4.1: Rule-Based Category Suggestion Engine

As a developer,
I want a backend service that suggests transaction categories based on keyword matching rules,
so that the system can provide initial category suggestions before machine learning is implemented.

**Acceptance Criteria:**

1. CategorizationService created with method: suggestCategory(description, amount, transactionType) returning CategorySuggestion DTO (categoryId, categoryName, confidence score 0.0-1.0)
2. Rule engine implemented using keyword/phrase matching: Transaction description analyzed for keywords (case-insensitive) mapped to categories
3. Example rules defined: "starbucks", "coffee", "restaurant", "dinner" → Food category; "uber", "lyft", "gas", "fuel" → Transport category; "rent", "mortgage" → Rent category; "salary", "paycheck", "bonus" → Salary category
4. Rules stored in database table (categorization_rules: id, keyword, category_id, transaction_type) seeded with initial ruleset (20-30 common patterns)
5. Amount-based heuristics: Large transactions (>$1000) suggest Rent/Salary, small amounts (<$10) suggest Food/Transport
6. Confidence score calculated based on match quality: exact keyword match = 0.9, partial match = 0.6, amount heuristic = 0.4
7. Service returns null or default "Other" category if no rules match (confidence < 0.3)
8. Multi-keyword matching: description can match multiple rules, highest confidence wins
9. Unit tests validate rule matching logic with various descriptions
10. Initial ruleset covers top 10 most common categories with 3-5 keywords each

### Story 4.2: Category Suggestion API Endpoint

As a developer,
I want an API endpoint that provides category suggestions for transaction descriptions,
so that the frontend can request suggestions during transaction entry.

**Acceptance Criteria:**

1. POST /api/categorization/suggest endpoint created accepting request body: description (required), amount (optional), transactionType (INCOME/EXPENSE, required)
2. Endpoint calls CategorizationService.suggestCategory() and returns CategorySuggestionResponse: categoryId, categoryName, confidence
3. Endpoint returns 200 OK with suggestion even if confidence is low (frontend decides whether to display)
4. If no suggestion available, returns 200 with null category or default "Other" category
5. Endpoint does not require authentication for MVP (suggestions are based on generic rules, not user-specific) - ALTERNATIVE: require auth for consistency
6. Request validation: description cannot be empty, transactionType must be valid enum
7. Unit tests validate endpoint behavior with various inputs
8. Integration test validates end-to-end suggestion flow
9. API documented with OpenAPI/Swagger annotations
10. Response time under 100ms for suggestion calculation (simple rule matching)

### Story 4.3: Frontend Category Suggestion Integration

As a user,
I want the transaction form to automatically suggest a category as I type the description,
so that I can quickly accept the suggestion instead of manually selecting from the dropdown.

**Acceptance Criteria:**

1. Add/Edit Transaction form modified to call POST /api/categorization/suggest as user types description (debounced to avoid excessive API calls, 500ms delay)
2. Suggestion displayed below or next to category dropdown: "Suggested category: Food" with confidence indicator (if confidence > 0.6)
3. User can click suggestion to auto-populate category dropdown, or ignore and manually select different category
4. Suggestion updates dynamically as description changes
5. Suggestion only shown if confidence score > 0.6 (configurable threshold)
6. Loading state displayed while fetching suggestion (subtle spinner, non-intrusive)
7. No suggestion shown if API fails or returns low confidence
8. Transaction type (income/expense) affects suggestions - switching type re-triggers suggestion
9. Amount field optionally used for suggestion (if filled)
10. Keyboard shortcut or Tab key can accept suggestion (accessibility)
11. Suggestion does not override user's manual category selection (user choice takes precedence)
12. Component tests validate suggestion display and acceptance behavior

### Story 4.4: User Correction Tracking for Learning

As a developer,
I want to track when users override or correct AI category suggestions,
so that the system can learn from these corrections and improve future suggestions for that user.

**Acceptance Criteria:**

1. Database table created: categorization_feedback (id, user_id, description, suggested_category_id, actual_category_id, transaction_id, created_at)
2. When user creates transaction: if suggestion was provided but user chose different category, record feedback entry
3. When user edits transaction and changes category: record feedback entry showing original and new category
4. FeedbackService created with method: recordFeedback(userId, description, suggestedCategoryId, actualCategoryId, transactionId)
5. Feedback recorded asynchronously (does not block transaction creation/update)
6. POST /api/transactions endpoint modified to accept optional suggestedCategoryId field for tracking
7. Feedback table indexed on user_id and description for efficient querying
8. Privacy consideration: feedback data tied to specific user (user-specific learning)
9. Unit tests validate feedback recording logic
10. Integration test validates feedback persisted when transaction created with different category than suggested

### Story 4.5: Personalized Suggestion Learning

As a user,
I want the category suggestions to improve over time based on my corrections,
so that the system becomes more accurate for my specific spending patterns and requires less manual intervention.

**Acceptance Criteria:**

1. CategorizationService.suggestCategory() modified to check user-specific feedback before applying generic rules
2. User-specific pattern matching: if user has consistently corrected "Amazon" to "Shopping" (3+ times), future "Amazon" transactions suggest "Shopping" with high confidence (0.95)
3. Feedback query retrieves user's past corrections for similar descriptions (fuzzy matching or keyword overlap)
4. User-specific suggestions prioritized over generic rules (higher confidence scores)
5. Learning threshold: pattern considered learned after 3+ consistent corrections
6. User-specific rules apply only to that user's suggestions (personalized learning)
7. Generic rules still used as fallback if no user-specific patterns exist
8. Performance optimization: user feedback cached or pre-aggregated to avoid N+1 queries
9. Unit tests validate personalized suggestion logic with mocked feedback data
10. Integration test validates learning: create transaction with suggestion, correct it 3 times, verify next suggestion uses corrected category

### Story 4.6: AI Categorization Performance Metrics and Monitoring

As a product manager,
I want to track AI categorization accuracy metrics,
so that I can measure whether the system is meeting the 75%+ accuracy target and identify areas for improvement.

**Acceptance Criteria:**

1. GET /api/analytics/categorization-metrics endpoint created (admin only or internal use)
2. Metrics calculated: total suggestions made, total accepted (user kept suggested category), total rejected (user changed category), accuracy rate (accepted / total)
3. Metrics broken down by category showing which categories have highest/lowest accuracy
4. Metrics filtered by date range and user (for per-user analysis)
5. Metrics include: average confidence score for accepted vs. rejected suggestions
6. Service layer calculates metrics from categorization_feedback table
7. Dashboard view (optional admin panel) displaying metrics with charts
8. Logging implemented to track suggestion requests, responses, and user actions
9. Target metric: 75%+ accuracy rate overall by MVP completion
10. Documentation created explaining how metrics are calculated and interpreted

### Story 4.7: Bulk Categorization for Existing Transactions

As a user,
I want to apply AI categorization to my existing uncategorized or incorrectly categorized transactions,
so that I can retroactively improve my data quality without manually editing each transaction.

**Acceptance Criteria:**

1. Backend endpoint created: POST /api/transactions/bulk-categorize accepting optional filter parameters (date range, current category)
2. Endpoint retrieves all user's transactions matching filters
3. For each transaction, calls CategorizationService.suggestCategory() and updates transaction if confidence > 0.7 threshold
4. Endpoint processes in batches (e.g., 100 transactions at a time) to avoid timeouts
5. Returns summary response: totalProcessed, totalUpdated, totalSkipped (low confidence)
6. Operation runs asynchronously with job ID returned, status queryable via GET /api/jobs/{jobId}
7. Frontend button added to transaction list: "Auto-Categorize All" or "Suggest Categories" triggering bulk operation
8. Progress indicator displayed during bulk operation
9. Completion notification shows summary: "Categorized 87 of 120 transactions"
10. User can review and manually adjust any automatic categorizations
11. Unit tests validate bulk processing logic
12. Integration test validates end-to-end bulk categorization

## Checklist Results Report

### PM Checklist Execution

**Checklist:** pm-checklist.md

**Execution Status:** ✅ Completed

**Results:**

✅ **Goals Section Complete:** Goals clearly articulate desired outcomes for users and the project, focusing on transaction tracking, AI categorization, visual insights, and BMAD methodology validation.

✅ **Background Context Complete:** Background provides concise summary of problem statement, target users, and MVP scope from Project Brief without redundancy.

✅ **Change Log Initialized:** Change log table created with initial entry documenting PRD creation.

✅ **Functional Requirements Defined:** 20 functional requirements specified covering authentication, transaction CRUD, categorization, dashboard summaries, visualizations, and filtering.

✅ **Non-Functional Requirements Defined:** 20 non-functional requirements specified covering performance, security, compatibility, testing, architecture, and code quality standards.

✅ **Requirements Traceable:** Each requirement can be traced to stories in epics and linked to acceptance criteria.

✅ **UI Design Goals Documented:** Comprehensive UI/UX section defines vision, interaction paradigms, core screens, accessibility (WCAG AA), branding guidelines, and responsive web targets.

✅ **Technical Assumptions Documented:** Complete technical stack specified: Angular 17+ frontend, Java 21/Spring Boot backend, PostgreSQL database, monorepo structure, Docker containerization, comprehensive testing strategy.

✅ **Epic List Defined:** 4 epics defined following agile best practices with sequential delivery: Foundation, Transaction Management, Dashboard & Analytics, AI Categorization.

✅ **Epic Sequencing Logical:** Each epic builds upon previous, delivering complete deployable functionality. Epic 1 establishes infrastructure while delivering auth functionality.

✅ **Stories Follow User Story Format:** All stories use "As a [user], I want [action], so that [benefit]" format.

✅ **Stories Sequenced Properly:** Stories within each epic follow logical sequence with no forward dependencies.

✅ **Acceptance Criteria Clear and Testable:** All stories include numbered, specific, testable acceptance criteria defining "done."

✅ **Story Sizing Appropriate:** Stories sized for AI agent completion in single focused session (2-4 hour equivalent).

✅ **Cross-Cutting Concerns Addressed:** Logging, security, testing integrated throughout stories rather than deferred to end.

✅ **No Orphaned Requirements:** All functional and non-functional requirements mapped to stories in epics.

⚠️ **Elicitation Performed:** YOLO mode selected - comprehensive draft created for user review. User can request elicitation on any section if desired.

✅ **Next Steps Defined:** Architect and UX expert prompts prepared for handoff to next phase.

**Overall Assessment:** PRD is comprehensive, well-structured, and ready for architectural design phase. All BMAD standards followed. Technical stack clearly defined (Angular/Spring Boot). Epic and story breakdown delivers MVP within 8-12 week timeline with logical sequencing.

## Next Steps

### UX Expert Prompt

**Handoff to UX/Design Architect:**

We have completed the Smart Budget App PRD with comprehensive UI/UX design goals. Please review the "User Interface Design Goals" section and create detailed UX specifications including:

1. Wireframes for core screens: Login/Register, Dashboard, Transaction List, Add/Edit Transaction Form, Analytics/Charts View
2. Component library recommendations (Angular Material vs. Tailwind CSS evaluation)
3. Responsive breakpoint specifications with mobile/tablet/desktop layouts
4. Color palette and typography system implementation details
5. Accessibility implementation checklist for WCAG AA compliance
6. User flow diagrams for key journeys: First-time user onboarding, Add transaction, View insights

Reference the PRD sections: User Interface Design Goals, Functional Requirements, and Epic 3 (Dashboard & Visual Analytics) for detailed feature specifications.

### Architect Prompt

**Handoff to Technical Architect:**

We have completed the Smart Budget App PRD with full technical stack and requirements definition. Please review and create the architecture document covering:

1. **System Architecture Diagram:** Angular SPA, Spring Boot REST API, PostgreSQL database, JWT authentication flow
2. **Database Schema Design:** Detailed ERD with all tables (users, transactions, categories, categorization_rules, categorization_feedback), relationships, indexes, and constraints
3. **API Contract Definition:** OpenAPI specification for all REST endpoints with request/response schemas
4. **Component Architecture:** Angular module structure (feature modules, shared modules, core module), Spring Boot package structure (controllers, services, repositories, DTOs)
5. **Security Architecture:** JWT token flow, password hashing, CORS configuration, authorization checks
6. **Deployment Architecture:** Docker container strategy, CI/CD pipeline design (GitHub Actions), environment configuration
7. **AI Categorization Architecture:** Rule engine design, feedback loop mechanism, future ML integration path
8. **Testing Strategy Implementation:** TestContainers setup, unit/integration test structure, coverage tooling

Reference the PRD sections: Technical Assumptions, Requirements (Functional and Non-Functional), and all Epic stories for detailed specifications.

**Technology Stack Summary:**
- Frontend: Angular 17+, TypeScript, Tailwind CSS or Angular Material, Chart.js/ngx-charts
- Backend: Java 21, Spring Boot 3.x, Hibernate ORM, Gradle
- Database: PostgreSQL 15+, Flyway migrations
- Infrastructure: Docker, GitHub Actions, AWS/GCP/Azure (TBD)
- Testing: JUnit/Mockito, Jasmine/Karma, TestContainers

Begin architecture creation following BMAD methodology and template: `docs/architecture.md`

---

*PRD Version 1.0 - Generated 2025-11-11*
*Following BMAD Methodology Standards*
