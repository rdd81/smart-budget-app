# Story 1.1: Project Initialization and Repository Structure

Status: review

## Story

As a developer,
I want the monorepo project structure initialized with both Angular and Spring Boot applications,
so that the team has a consistent foundation to build features and can start development immediately.

## Acceptance Criteria

1. Monorepo directory structure created with `frontend/` and `backend/` subdirectories
2. Angular 17+ application generated using Angular CLI in `frontend/` directory with TypeScript strict mode enabled
3. Spring Boot 3.x application generated (using Spring Initializr or Gradle init) in `backend/` directory with Java 21, Spring Web, Spring Data JPA, Spring Security, and PostgreSQL dependencies
4. Root-level README.md created with project overview, technology stack, and basic getting started instructions
5. .gitignore configured for both Node.js and Java/Gradle artifacts
6. Both frontend and backend applications can run independently (Angular dev server on port 4200, Spring Boot on port 8080)
7. Gradle wrapper included in backend/ for consistent build experience
8. npm/pnpm package.json includes Angular 17+ and required dependencies
9. Basic folder structure follows BMAD standards with docs/ directory created

## Tasks / Subtasks

- [x] Initialize monorepo structure (AC: 1, 9)
  - [x] Create root project directory structure (frontend/, backend/, docs/)
  - [x] Initialize Git repository with appropriate .gitignore for Node.js and Java
  - [x] Create docs/ directory following BMAD standards

- [x] Set up Angular frontend application (AC: 2, 8)
  - [x] Install Node.js 20 LTS and npm
  - [x] Run Angular CLI: `ng new smart-budget-app --directory=frontend --style=scss --routing=true`
  - [x] Enable TypeScript strict mode in tsconfig.json
  - [x] Verify package.json includes Angular 17+ dependencies
  - [x] Test: Run `ng serve` and verify app loads at http://localhost:4200

- [x] Set up Spring Boot backend application (AC: 3, 7)
  - [x] Install Java 21 LTS (Amazon Corretto or OpenJDK)
  - [x] Generate Spring Boot project using Spring Initializr with dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver
  - [x] Configure Gradle wrapper (gradlew, gradlew.bat)
  - [x] Verify build.gradle includes Spring Boot 3.2+, Java 21, required dependencies
  - [x] Test: Run `./gradlew bootRun` and verify app starts on port 8080

- [x] Create root-level README.md (AC: 4)
  - [x] Document project overview and purpose
  - [x] List complete technology stack (Angular 17+, Spring Boot 3.2+, PostgreSQL 15+, Docker)
  - [x] Add prerequisites section (Java 21, Node 20, Docker 24+)
  - [x] Include basic getting started instructions
  - [x] Add project structure overview

- [x] Verify independent application startup (AC: 6)
  - [x] Start frontend with `cd frontend && npm start` - verify http://localhost:4200
  - [x] Start backend with `cd backend && ./gradlew bootRun` - verify http://localhost:8080
  - [x] Confirm no port conflicts or startup errors

- [x] Write unit tests for initial setup (AC: Testing validation)
  - [x] Frontend: Verify Angular app component renders successfully
  - [x] Backend: Verify Spring Boot application context loads successfully
  - [x] Document test execution commands in README

## Dev Notes

### Requirements Context

**Epic Goal:** Establish technical foundation for Smart Budget App with project structure, development environment, containerization, CI/CD, database, and authentication.

**Story Focus:** Initialize monorepo with Angular 17+ frontend and Spring Boot 3.2+ backend, ensuring both applications can run independently as foundation for subsequent stories.

**Key Architectural Constraints:**
- **Monorepo Structure:** Simple directory-based monorepo (no Nx/Turborepo) with frontend/ and backend/ subdirectories [Source: [architecture.md](../architecture.md#Repository-Structure)]
- **Frontend:** Angular 17+ with TypeScript strict mode, component-based architecture [Source: [architecture.md](../architecture.md#Tech-Stack)]
- **Backend:** Java 21 LTS, Spring Boot 3.2+, layered architecture (Controller → Service → Repository) [Source: [architecture.md](../architecture.md#High-Level-Architecture)]
- **Version Control:** Git with .gitignore for Node.js and Java/Gradle artifacts

### Project Structure Notes

**Expected Directory Structure:**
```
smart-budget-app/
├── frontend/              # Angular 17+ application
│   ├── src/
│   │   ├── app/          # Application code
│   │   ├── assets/       # Static assets
│   │   └── environments/ # Environment configs
│   ├── package.json      # npm dependencies
│   ├── tsconfig.json     # TypeScript config (strict mode)
│   └── angular.json      # Angular CLI config
├── backend/               # Spring Boot 3.2+ application
│   ├── src/
│   │   ├── main/java/    # Java source code
│   │   └── test/java/    # Test source code
│   ├── build.gradle      # Gradle build config
│   ├── gradlew           # Gradle wrapper (Unix)
│   └── gradlew.bat       # Gradle wrapper (Windows)
├── docs/                  # BMAD documentation
│   ├── prd/              # Product requirements
│   ├── architecture/     # Architecture docs
│   └── sprint-artifacts/ # Stories, tech specs
├── .gitignore            # Combined Node.js + Java ignores
└── README.md             # Project documentation
```

**Technology Versions:**
- Node.js: 20 LTS
- npm: 10+
- Angular: 17+ (latest stable)
- Angular CLI: 17+
- Java: 21 LTS (Amazon Corretto or OpenJDK)
- Spring Boot: 3.2+ (latest stable)
- Gradle: 8.5+ (via wrapper)

**Testing Requirements:**
- Frontend: Jasmine/Karma for unit tests (60%+ coverage target)
- Backend: JUnit 5/Mockito for unit tests (70%+ coverage target)
- Both apps must start successfully without errors

### Learnings from Previous Story

First story in epic - no predecessor context

### References

- **Architecture:** [docs/architecture.md](../architecture.md) - Repository structure, tech stack, architectural patterns
- **Tech Stack:** [docs/architecture.md#Tech-Stack](../architecture.md#Tech-Stack) - Angular 17+, Spring Boot 3.2+, Java 21, Node 20
- **PRD Epic 1:** [docs/prd/epic-1-foundation-core-infrastructure.md](../prd/epic-1-foundation-core-infrastructure.md) - Epic goal and story breakdown
- **Tech Spec Epic 1:** [docs/sprint-artifacts/tech-spec-epic-1.md](../sprint-artifacts/tech-spec-epic-1.md) - Detailed technical specifications, dependencies, system architecture alignment

## Dev Agent Record

### Context Reference

[1-1-project-initialization-and-repository-structure.context.xml](1-1-project-initialization-and-repository-structure.context.xml)

### Agent Model Used

Not yet implemented

### Debug Log References

### Completion Notes List

**Implementation Summary:**
Successfully initialized Smart Budget App monorepo with Angular 20.3.0 frontend and Spring Boot 3.4.1 backend. Both applications run independently with all acceptance criteria met.

**Key Accomplishments:**
1. **Monorepo Structure:** Created frontend/, backend/, docs/ directories with proper Git configuration
2. **Angular Frontend:** Generated Angular 20.3.0 app with TypeScript 5.9.2 strict mode, SCSS, routing, and passing tests
3. **Spring Boot Backend:** Generated Spring Boot 3.4.1 app with Java 21, Spring Web, Data JPA, Security, PostgreSQL driver, and passing tests
4. **Documentation:** Created comprehensive 220-line README.md with tech stack, prerequisites, setup instructions, and test commands
5. **Testing Infrastructure:** Frontend (Jasmine/Karma) and backend (JUnit 5) tests passing
6. **Independent Startup:** Verified both apps run on separate ports (4200, 8080) without conflicts

**Technical Decisions:**
- Temporarily disabled datasource auto-configuration in `application.properties` to allow backend startup without database (database setup deferred to Story 1.3)
- Used Angular CLI 20.3.10 which automatically enables TypeScript strict mode
- Installed Angular CLI globally for consistent build experience
- Used Gradle wrapper 8.14.3 for consistent Java build experience

**Challenges Resolved:**
- Port conflicts during testing: Killed stale processes on ports 4200 and 8080
- Spring Boot datasource error: Added auto-configuration exclusion for JPA until Story 1.3

**Test Results:**
- Frontend: 2/2 tests passing (ChromeHeadless)
- Backend: BUILD SUCCESSFUL (context loads)

**Next Story Dependencies:**
Story 1.2 (Docker Containerization) can proceed immediately with this foundation.

### File List

**Modified Files:**
- [.gitignore](.gitignore) - Added comprehensive Node.js and Java/Gradle ignore patterns
- [backend/src/main/resources/application.properties](../../backend/src/main/resources/application.properties) - Disabled datasource auto-configuration (temporary until Story 1.3)
- [docs/sprint-artifacts/sprint-status.yaml](sprint-status.yaml) - Updated story status to in-progress → review

**Created Files:**
- [README.md](../../README.md) - Project overview, tech stack, prerequisites, getting started instructions (220 lines)
- [frontend/](../../frontend/) - Complete Angular 20.3.0 application with TypeScript strict mode
  - `package.json` - Angular 20.3.0, TypeScript 5.9.2, RxJS 7.8.0 dependencies
  - `tsconfig.json` - Strict mode enabled
  - `src/app/app.spec.ts` - Default component tests (2 passing)
  - Complete Angular CLI-generated structure
- [backend/](../../backend/) - Complete Spring Boot 3.4.1 application with Java 21
  - `build.gradle` - Spring Boot 3.4.1, Java 21, required dependencies
  - `gradlew`, `gradlew.bat` - Gradle 8.14.3 wrapper
  - `src/main/java/com/smartbudget/` - Main application package
  - `src/test/java/com/smartbudget/SmartBudgetAppApplicationTests.java` - Context load test (passing)
