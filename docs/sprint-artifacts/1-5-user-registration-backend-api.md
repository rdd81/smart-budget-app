# Story 1.5: User Registration Backend API

Status: review

## Story

As a new user,
I want to register for an account with my email and password,
so that I can securely access the application and store my personal financial data.

## Acceptance Criteria

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

## Tasks / Subtasks

- [ ] Create DTOs for registration (AC: 1, 2, 6)
  - [ ] Create RegisterRequest DTO with email and password fields
  - [ ] Add @Email validation to email field
  - [ ] Add @NotBlank and @Size(min=8) validation to password field
  - [ ] Create UserResponse DTO with id, email, createdAt, updatedAt (exclude password)
  - [ ] Test: Verify validation annotations work with invalid input

- [ ] Implement AuthService for registration logic (AC: 4, 5, 7)
  - [ ] Create AuthService class with @Service annotation
  - [ ] Inject UserRepository and PasswordEncoder (BCrypt)
  - [ ] Implement register(RegisterRequest) method with @Transactional
  - [ ] Check email uniqueness using userRepository.existsByEmail()
  - [ ] Hash password using passwordEncoder.encode() with BCrypt strength 10
  - [ ] Create User entity and save to database
  - [ ] Return UserResponse DTO (map entity to DTO)
  - [ ] Test: Unit test with mocked repository

- [ ] Create AuthController for registration endpoint (AC: 1, 3, 6, 8)
  - [ ] Create AuthController class with @RestController and @RequestMapping("/api/auth")
  - [ ] Create register(@Valid @RequestBody RegisterRequest) method
  - [ ] Annotate with @PostMapping("/register")
  - [ ] Return ResponseEntity with 201 Created status
  - [ ] Add exception handler for duplicate email (throw custom exception)
  - [ ] Add @ControllerAdvice for global exception handling
  - [ ] Handle validation errors (return 400 Bad Request)
  - [ ] Test: Integration test with MockMvc

- [ ] Configure BCrypt password encoder (AC: 4)
  - [ ] Create SecurityConfig class with @Configuration
  - [ ] Define @Bean for PasswordEncoder returning BCryptPasswordEncoder(10)
  - [ ] Verify work factor is 10 (default strength)
  - [ ] Test: Verify encoded passwords are different for same input

- [ ] Add Swagger/OpenAPI documentation (AC: 11)
  - [ ] Add springdoc-openapi dependency to build.gradle
  - [ ] Add @Tag annotation to AuthController
  - [ ] Add @Operation annotation to register endpoint
  - [ ] Add @ApiResponses for 201 Created and 400 Bad Request
  - [ ] Configure Swagger UI endpoint
  - [ ] Test: Access Swagger UI at /swagger-ui.html

- [ ] Create unit tests for AuthService (AC: 9)
  - [ ] Create AuthServiceTest class with @ExtendWith(MockitoExtension.class)
  - [ ] Mock UserRepository and PasswordEncoder
  - [ ] Test successful registration flow
  - [ ] Test duplicate email throws exception
  - [ ] Test password is hashed before storage
  - [ ] Verify repository.save() called with correct data
  - [ ] Test: All unit tests pass with >70% coverage

- [ ] Create integration test for registration (AC: 10)
  - [ ] Create AuthControllerIntegrationTest with @SpringBootTest
  - [ ] Use TestContainers for PostgreSQL
  - [ ] Test successful registration with valid data
  - [ ] Verify user persisted to database
  - [ ] Test duplicate email returns 400 Bad Request
  - [ ] Test invalid email format returns 400 Bad Request
  - [ ] Test password too short returns 400 Bad Request
  - [ ] Verify password is hashed (not plain text in DB)
  - [ ] Verify createdAt and updatedAt timestamps set
  - [ ] Test: All integration tests pass

## Dev Notes

### Requirements Context

**Epic Goal:** Establish technical foundation for Smart Budget App with project structure, development environment, containerization, CI/CD, database, and authentication.

**Story Focus:** Implement user registration backend API endpoint that accepts email and password, validates input, hashes password with BCrypt, stores user in PostgreSQL database, and returns user details. This is the first step in the authentication system, enabling users to create accounts.

**Key Architectural Constraints:**
- **Password Hashing:** BCrypt with work factor 10 [Source: [tech-spec-epic-1.md](tech-spec-epic-1.md#Security)]
- **Validation:** Bean Validation API (@Email, @NotBlank, @Size) [Source: [architecture.md](../architecture.md#Tech-Stack)]
- **Transaction Management:** @Transactional for atomic operations [Source: [tech-spec-epic-1.md](tech-spec-epic-1.md#Services-and-Modules)]
- **API Documentation:** OpenAPI/Swagger annotations [Source: PRD Epic 1 AC 11]
- **Error Handling:** Global @ControllerAdvice for consistent error responses [Source: [tech-spec-epic-1.md](tech-spec-epic-1.md#Non-Functional-Requirements)]

### Project Structure Notes

**Expected Backend Files:**
```
backend/
└── src/
    └── main/
        └── java/com/smartbudget/
            ├── config/
            │   ├── SecurityConfig.java           # BCrypt PasswordEncoder bean
            │   └── OpenApiConfig.java            # Swagger configuration
            ├── controller/
            │   └── AuthController.java           # POST /api/auth/register endpoint
            ├── dto/
            │   ├── RegisterRequest.java          # Request DTO with validation
            │   └── UserResponse.java             # Response DTO (no password)
            ├── service/
            │   └── AuthService.java              # Registration business logic
            ├── exception/
            │   ├── DuplicateEmailException.java  # Custom exception
            │   └── GlobalExceptionHandler.java   # @ControllerAdvice
            └── entity/
                └── User.java                     # Already exists from Story 1.3
            └── repository/
                └── UserRepository.java           # Already exists from Story 1.3
```

**DTO Structure:**

RegisterRequest:
```java
public class RegisterRequest {
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

UserResponse:
```java
public class UserResponse {
    private UUID id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // passwordHash intentionally excluded
}
```

**Error Response Format:**
```json
{
  "timestamp": "2025-11-16T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

### Learnings from Previous Story

**From Story 1.4: CI/CD Pipeline Setup (Status: review)**

**Testing Infrastructure:**
- JaCoCo code coverage configured with 70% minimum threshold
- Integration tests use TestContainers for PostgreSQL
- GitHub Actions workflow runs tests automatically on PR
- Coverage reports generated in build/reports/jacoco/test/

**Key Insight for This Story:**
All new code must meet 70% coverage threshold or CI build will fail. Ensure comprehensive unit and integration tests for AuthService and AuthController. Use @SpringBootTest with TestContainers for end-to-end registration flow testing.

**Testing Best Practices:**
1. Unit tests: Mock dependencies (UserRepository, PasswordEncoder)
2. Integration tests: Use real database with TestContainers
3. Test all validation scenarios (invalid email, short password, duplicate email)
4. Verify password hashing (compare encoded vs plain text)
5. Verify database persistence (query user after registration)

[Source: [1-4-ci-cd-pipeline-setup.md](1-4-ci-cd-pipeline-setup.md#Completion-Notes-List)]

**From Story 1.3: Database Schema (Status: review)**

**User Entity Already Exists:**
- User entity created with UUID id, email (unique), passwordHash, createdAt, updatedAt
- UserRepository interface with findByEmail() and existsByEmail() methods
- JPA Auditing enabled (@CreatedDate, @LastModifiedDate work automatically)
- PostgreSQL database with users table and email index

**Files to Reuse:**
1. **backend/src/main/java/com/smartbudget/entity/User.java** - Already has all required fields
2. **backend/src/main/java/com/smartbudget/repository/UserRepository.java** - Has existsByEmail() method needed for validation

[Source: [1-3-database-schema-and-migration-framework.md](1-3-database-schema-and-migration-framework.md#Completion-Notes-List)]

### References

- **PRD Epic 1:** [docs/prd/epic-1-foundation-core-infrastructure.md](../prd/epic-1-foundation-core-infrastructure.md#Story-1.5) - Story 1.5 acceptance criteria
- **Tech Spec Epic 1:** [docs/sprint-artifacts/tech-spec-epic-1.md](tech-spec-epic-1.md#Data-Models-and-Contracts) - RegisterRequest, UserResponse DTOs, API endpoints
- **Architecture:** [docs/architecture.md](../architecture.md#Tech-Stack) - Bean Validation, Spring Security, OpenAPI
- **Story 1.3:** [docs/sprint-artifacts/1-3-database-schema-and-migration-framework.md](1-3-database-schema-and-migration-framework.md) - User entity and UserRepository
- **Story 1.4:** [docs/sprint-artifacts/1-4-ci-cd-pipeline-setup.md](1-4-ci-cd-pipeline-setup.md) - Testing infrastructure and coverage requirements
- **Spring Security Docs:** https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html - BCrypt configuration
- **Bean Validation Docs:** https://beanvalidation.org/2.0/spec/ - Validation annotations

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Sonnet 4.5 (claude-sonnet-4-5-20250929)

### Debug Log References

### Completion Notes List

**Implementation Status:** Complete - All acceptance criteria implemented

**Summary:**
Successfully implemented user registration backend API endpoint that accepts email and password, validates input, hashes password with BCrypt, stores user in PostgreSQL database, and returns user details. This is the first step in the authentication system, enabling users to create accounts.

**Key Implementation Details:**

**1. DTOs Created:**
- **RegisterRequest.java**: Request DTO with validation annotations
  - @Email validation for email field
  - @NotBlank and @Size(min=8) validation for password field
- **UserResponse.java**: Response DTO excluding passwordHash for security
  - Fields: id, email, createdAt, updatedAt

**2. Service Layer:**
- **AuthService.java**: Registration business logic with @Transactional
  - Email uniqueness check using userRepository.existsByEmail()
  - Password hashing using BCrypt with work factor 10
  - User entity creation and persistence
  - Returns UserResponse DTO

**3. Controller Layer:**
- **AuthController.java**: POST /api/auth/register endpoint
  - Accepts @Valid @RequestBody RegisterRequest
  - Returns 201 Created with UserResponse
  - Includes OpenAPI/Swagger annotations (@Tag, @Operation, @ApiResponses)

**4. Configuration:**
- **SecurityConfig.java**: BCrypt PasswordEncoder bean with work factor 10
- **OpenApiConfig.java**: Swagger/OpenAPI configuration for API documentation

**5. Exception Handling:**
- **DuplicateEmailException.java**: Custom exception for email uniqueness violations
- **ErrorResponse.java**: Standard error response structure with timestamp, status, error, message, path
- **GlobalExceptionHandler.java**: @ControllerAdvice with handlers for:
  - DuplicateEmailException → 400 Bad Request
  - MethodArgumentNotValidException → 400 Bad Request with field errors
  - Generic Exception → 500 Internal Server Error

**6. Testing:**
- **AuthServiceTest.java**: Unit tests with Mockito
  - Test successful registration flow
  - Test duplicate email throws DuplicateEmailException
  - Test password hashing before storage
  - Test repository.save() called with correct data
  - Coverage: 4 test methods covering all service logic

- **AuthControllerIntegrationTest.java**: End-to-end tests with TestContainers
  - Test successful registration with valid data (201 Created)
  - Test duplicate email returns 400 Bad Request
  - Test invalid email format returns 400 Bad Request
  - Test password too short returns 400 Bad Request
  - Test blank email/password returns 400 Bad Request
  - Test password is hashed (BCrypt format, not plain text)
  - Test timestamps (createdAt, updatedAt) are set
  - Coverage: 8 test methods covering all validation scenarios

**7. Dependencies Added:**
- spring-boot-starter-validation (Bean Validation API)
- springdoc-openapi-starter-webmvc-ui:2.3.0 (OpenAPI/Swagger)

**API Documentation:**
- Swagger UI available at: http://localhost:8080/swagger-ui.html
- OpenAPI JSON available at: http://localhost:8080/v3/api-docs

**Success Response Example (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "createdAt": "2025-11-16T10:30:00",
  "updatedAt": "2025-11-16T10:30:00"
}
```

**Error Response Example (400 Bad Request):**
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

**All Acceptance Criteria Met:**
1. ✓ POST /api/auth/register endpoint created
2. ✓ RegisterRequest DTO with validation annotations
3. ✓ Email uniqueness validation enforced (400 Bad Request)
4. ✓ Password hashed using BCrypt with work factor 10
5. ✓ User entity persisted with UUID, hashed password, timestamps
6. ✓ Returns 201 Created with UserResponse (excludes password)
7. ✓ AuthService implements registration with @Transactional
8. ✓ Controller includes error handling for validation and duplicates
9. ✓ Unit tests cover AuthService with mocked repository
10. ✓ Integration test validates end-to-end flow
11. ✓ API documented with OpenAPI/Swagger annotations

### File List

**Created Files:**
1. backend/src/main/java/com/smartbudget/dto/RegisterRequest.java - Request DTO with validation
2. backend/src/main/java/com/smartbudget/dto/UserResponse.java - Response DTO (no password)
3. backend/src/main/java/com/smartbudget/service/AuthService.java - Registration business logic
4. backend/src/main/java/com/smartbudget/controller/AuthController.java - POST /api/auth/register endpoint
5. backend/src/main/java/com/smartbudget/config/SecurityConfig.java - BCrypt PasswordEncoder bean
6. backend/src/main/java/com/smartbudget/config/OpenApiConfig.java - Swagger configuration
7. backend/src/main/java/com/smartbudget/exception/DuplicateEmailException.java - Custom exception
8. backend/src/main/java/com/smartbudget/exception/ErrorResponse.java - Standard error structure
9. backend/src/main/java/com/smartbudget/exception/GlobalExceptionHandler.java - @ControllerAdvice
10. backend/src/test/java/com/smartbudget/service/AuthServiceTest.java - Unit tests for AuthService
11. backend/src/test/java/com/smartbudget/controller/AuthControllerIntegrationTest.java - Integration tests

**Modified Files:**
1. backend/build.gradle - Added spring-boot-starter-validation and springdoc-openapi dependencies
