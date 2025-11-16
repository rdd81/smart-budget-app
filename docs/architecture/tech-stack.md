# Technology Stack

This document provides a comprehensive overview of all technologies, frameworks, libraries, and tools used in the Smart Budget App.

## Table of Contents

- [Frontend Technologies](#frontend-technologies)
- [Backend Technologies](#backend-technologies)
- [Database](#database)
- [DevOps and Infrastructure](#devops-and-infrastructure)
- [Development Tools](#development-tools)
- [Testing Frameworks](#testing-frameworks)
- [Technology Rationale](#technology-rationale)
- [Browser and Runtime Requirements](#browser-and-runtime-requirements)

## Frontend Technologies

### Core Framework

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Angular** | 17.0+ | Progressive web application framework | https://angular.dev |
| **TypeScript** | 5.2+ | Statically typed JavaScript superset | https://www.typescriptlang.org |
| **RxJS** | 7.8+ | Reactive programming library | https://rxjs.dev |

### UI and Styling

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Tailwind CSS** | 3.4+ | Utility-first CSS framework | https://tailwindcss.com |
| **PostCSS** | 8.4+ | CSS transformation tool | https://postcss.org |

### Build Tools

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Angular CLI** | 17.0+ | Command line interface for Angular | https://angular.dev/cli |
| **esbuild** | - | Fast JavaScript bundler (via Angular) | https://esbuild.github.io |
| **npm** | 10+ | Package manager | https://www.npmjs.com |

### Major Dependencies

```json
{
  "@angular/animations": "^17.0.0",
  "@angular/common": "^17.0.0",
  "@angular/compiler": "^17.0.0",
  "@angular/core": "^17.0.0",
  "@angular/forms": "^17.0.0",
  "@angular/platform-browser": "^17.0.0",
  "@angular/platform-browser-dynamic": "^17.0.0",
  "@angular/router": "^17.0.0",
  "rxjs": "^7.8.0",
  "tslib": "^2.6.0",
  "zone.js": "^0.14.0",
  "tailwindcss": "^3.4.0"
}
```

**Rationale:**
- **Angular 17+:** Latest stable version with improved performance, signals, and standalone components
- **Tailwind CSS:** Rapid UI development with utility-first approach, small production bundle size
- **TypeScript:** Type safety, improved developer experience, and better IDE support

## Backend Technologies

### Core Framework

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Spring Boot** | 3.2.x | Enterprise Java application framework | https://spring.io/projects/spring-boot |
| **Java** | 21 LTS | Programming language | https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html |

### Spring Framework Modules

| Module | Purpose |
|--------|---------|
| **Spring Web** | RESTful web services and MVC |
| **Spring Security** | Authentication and authorization |
| **Spring Data JPA** | Data access and ORM |
| **Spring Validation** | Request validation |

### Database and Persistence

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Hibernate ORM** | 6.4+ | Object-relational mapping | https://hibernate.org/orm |
| **Flyway** | 10.x | Database version control and migrations | https://flywaydb.org |
| **HikariCP** | 5.1+ | JDBC connection pool | https://github.com/brettwooldridge/HikariCP |
| **PostgreSQL Driver** | 42.7+ | JDBC driver for PostgreSQL | https://jdbc.postgresql.org |

### Security

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **JJWT** | 0.12+ | JSON Web Token creation and validation | https://github.com/jwtk/jjwt |
| **BCrypt** | - | Password hashing (via Spring Security) | - |

### API Documentation

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **SpringDoc OpenAPI** | 2.3+ | Swagger/OpenAPI documentation | https://springdoc.org |

### Build Tool

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Gradle** | 8.5+ | Build automation and dependency management | https://gradle.org |

### Major Dependencies

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.postgresql:postgresql'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-impl:0.12.3'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
}
```

**Rationale:**
- **Spring Boot 3.2:** Latest stable version with Java 21 support, improved performance, and native image support
- **Java 21 LTS:** Long-term support, modern language features (virtual threads, pattern matching), improved performance
- **PostgreSQL:** Enterprise-grade RDBMS with excellent JSON support and ACID compliance
- **Flyway:** Database version control with team collaboration support
- **JJWT:** Comprehensive JWT library with strong security features

## Database

### Primary Database

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **PostgreSQL** | 15.x | Relational database management system | https://www.postgresql.org |

**Rationale:**
- ACID compliance for financial data integrity
- Excellent performance for complex queries
- JSON/JSONB support for flexible data models
- Mature ecosystem and wide cloud provider support
- Open source with commercial support options

## DevOps and Infrastructure

### Containerization

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Docker** | 24.0+ | Container runtime | https://www.docker.com |
| **Docker Compose** | 2.20+ | Multi-container orchestration | https://docs.docker.com/compose |

### Web Server

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **NGINX** | 1.25+ Alpine | Static file serving and reverse proxy | https://nginx.org |

### Base Images

| Image | Purpose |
|-------|---------|
| **eclipse-temurin:21-jre-alpine** | Backend runtime (production) |
| **gradle:8.5-jdk21** | Backend build (multi-stage) |
| **node:20-alpine** | Frontend build (multi-stage) |
| **nginx:alpine** | Frontend serving (production) |
| **postgres:15-alpine** | Database |

**Rationale:**
- **Alpine Linux:** Minimal image size (5MB base) for faster deployments and reduced attack surface
- **Multi-stage builds:** Smaller production images by separating build and runtime dependencies
- **Official images:** Maintained by trusted sources with regular security updates

### CI/CD

| Technology | Purpose | Documentation |
|------------|---------|---------------|
| **GitHub Actions** | Continuous integration and deployment | https://github.com/features/actions |

## Development Tools

### IDEs and Editors

**Recommended:**
- **IntelliJ IDEA** (Backend) - Full Spring Boot support
- **VS Code** (Frontend/Full-stack) - Excellent TypeScript and Angular support
- **WebStorm** (Frontend) - Advanced JavaScript/TypeScript IDE

### Code Quality

| Tool | Purpose |
|------|---------|
| **ESLint** | JavaScript/TypeScript linting |
| **Prettier** | Code formatting |
| **SonarQube** (Optional) | Code quality analysis |

### Version Control

| Technology | Purpose |
|------------|---------|
| **Git** | Distributed version control |
| **GitHub** | Repository hosting and collaboration |

## Testing Frameworks

### Frontend Testing

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **Jasmine** | 5.1+ | Testing framework | https://jasmine.github.io |
| **Karma** | 6.4+ | Test runner | https://karma-runner.github.io |

### Backend Testing

| Technology | Version | Purpose | Documentation |
|------------|---------|---------|---------------|
| **JUnit** | 5.10+ | Unit testing framework | https://junit.org/junit5 |
| **Mockito** | 5.10+ | Mocking framework | https://site.mockito.org |
| **AssertJ** | 3.25+ | Fluent assertions | https://assertj.github.io/doc |
| **Testcontainers** | 1.19+ | Integration testing with containers | https://testcontainers.com |
| **Spring Boot Test** | - | Spring testing utilities | https://spring.io/guides/gs/testing-web |

**Test Coverage Tools:**
- **JaCoCo** (Backend) - Java code coverage
- **Istanbul** (Frontend) - JavaScript code coverage

**Rationale:**
- **Testcontainers:** Real database testing without mocking, ensures migrations work correctly
- **AssertJ:** Readable test assertions with better IDE support than JUnit assertions
- **Mockito:** Industry-standard mocking for unit tests

## Technology Rationale

### Architecture Decisions

| Decision | Rationale |
|----------|-----------|
| **Monorepo** | Simplified development, shared tooling, atomic cross-repo commits |
| **Microservices** (Future) | Current: Monolith for simplicity; Future: Scale independently |
| **Stateless API** | Horizontal scaling, cloud-native, JWT-based auth |
| **SPA Frontend** | Better UX, reduced server load, mobile-ready architecture |

### Security Decisions

| Decision | Rationale |
|----------|-----------|
| **JWT Authentication** | Stateless, scalable, standard-based |
| **BCrypt Hashing** | Industry standard, adaptive work factor, resistant to rainbow tables |
| **CORS Whitelisting** | Prevents unauthorized cross-origin requests |
| **HTTPS Enforcement** | Protects data in transit |
| **Non-root Containers** | Reduces attack surface, defense in depth |

### Performance Decisions

| Decision | Rationale |
|----------|-----------|
| **Connection Pooling** | Reduces database connection overhead |
| **Multi-stage Docker Builds** | Smaller images (backend: ~300MB vs 1GB+) |
| **Angular AOT Compilation** | Faster runtime, smaller bundles |
| **GZIP Compression** | Reduces bandwidth by 70%+ for text assets |

## Browser and Runtime Requirements

### Frontend Browser Support

**Supported Browsers:**
- Chrome/Edge 90+ (Chromium)
- Firefox 88+
- Safari 14+
- Mobile: iOS Safari 14+, Chrome Android 90+

**Required JavaScript Features:**
- ES2022 support
- Web Storage API (localStorage)
- Fetch API
- CSS Grid and Flexbox

### Backend Runtime Requirements

**Java Runtime:**
- Java 21 or higher (LTS recommended)
- Minimum heap: 512MB
- Recommended heap: 1-4GB depending on load

**Operating System:**
- Linux (recommended): Ubuntu 22.04+, Amazon Linux 2023, Alpine Linux
- Windows Server 2019+
- macOS 12+ (development only)

## Version Update Policy

### Long-Term Support (LTS) Versions

- **Java:** Java 21 LTS (support until September 2029)
- **Node.js:** Node.js 20 LTS (support until April 2026)
- **PostgreSQL:** Version 15 (support until November 2027)

### Regular Updates

- **Angular:** Upgrade to latest minor version quarterly
- **Spring Boot:** Upgrade to latest patch version monthly, minor version quarterly
- **Dependencies:** Review and update monthly for security patches

### Security Updates

All security vulnerabilities are patched within:
- **Critical:** 24 hours
- **High:** 7 days
- **Medium:** 30 days
- **Low:** Next scheduled maintenance window

## References

- **Spring Initializr:** https://start.spring.io
- **Angular CLI:** https://angular.dev/cli
- **Tailwind CSS:** https://tailwindcss.com
- **Docker Hub:** https://hub.docker.com
- **PostgreSQL Documentation:** https://www.postgresql.org/docs
