# Story 1.6: User Login and JWT Authentication

Status: review

## Story

As a registered user,
I want to log in with my email and password and receive an authentication token,
so that I can securely access protected features and my personal data.

## Acceptance Criteria

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

## Tasks / Subtasks

- [ ] Add JWT dependencies to build.gradle (AC: 5, 6, 8)
  - [ ] Add io.jsonwebtoken:jjwt-api dependency
  - [ ] Add io.jsonwebtoken:jjwt-impl runtime dependency
  - [ ] Add io.jsonwebtoken:jjwt-jackson runtime dependency
  - [ ] Test: Verify dependencies resolve successfully

- [ ] Create DTOs for login (AC: 1, 2, 7)
  - [ ] Create LoginRequest DTO with email and password fields
  - [ ] Add @Email and @NotBlank validation to email field
  - [ ] Add @NotBlank validation to password field
  - [ ] Create AuthResponse DTO with accessToken and UserResponse fields
  - [ ] Test: Verify validation annotations work

- [ ] Create JwtService for token operations (AC: 5, 6, 8)
  - [ ] Create JwtService class with @Service annotation
  - [ ] Add method generateToken(User user) returning JWT string
  - [ ] Add method extractUserId(String token) returning UUID
  - [ ] Add method extractEmail(String token) returning String
  - [ ] Add method validateToken(String token) returning boolean
  - [ ] Load JWT secret from environment variable (JWT_SECRET)
  - [ ] Set token expiration to 24 hours
  - [ ] Use HS256 algorithm for signing
  - [ ] Test: Unit test token generation and validation

- [ ] Add login method to AuthService (AC: 3, 4, 5, 7)
  - [ ] Create login(LoginRequest) method in AuthService
  - [ ] Query user by email using userRepository.findByEmail()
  - [ ] If not found, throw InvalidCredentialsException
  - [ ] Verify password using passwordEncoder.matches()
  - [ ] If mismatch, throw InvalidCredentialsException
  - [ ] Generate JWT token using jwtService.generateToken()
  - [ ] Return AuthResponse with token and UserResponse
  - [ ] Test: Unit test with mocked repository and password encoder

- [ ] Create InvalidCredentialsException (AC: 4)
  - [ ] Create InvalidCredentialsException extending RuntimeException
  - [ ] Add handler in GlobalExceptionHandler returning 401 Unauthorized
  - [ ] Use generic message: "Invalid credentials"
  - [ ] Test: Verify exception returns correct status and message

- [ ] Add login endpoint to AuthController (AC: 1, 12)
  - [ ] Create login(@Valid @RequestBody LoginRequest) method
  - [ ] Annotate with @PostMapping("/login")
  - [ ] Call authService.login() and return ResponseEntity with 200 OK
  - [ ] Add @Operation and @ApiResponses Swagger annotations
  - [ ] Test: Integration test with MockMvc

- [ ] Configure Spring Security for JWT (AC: 9)
  - [ ] Create JwtAuthenticationFilter extending OncePerRequestFilter
  - [ ] Extract JWT from Authorization header (Bearer token)
  - [ ] Validate token using jwtService.validateToken()
  - [ ] Extract user ID and set authentication in SecurityContext
  - [ ] Update SecurityConfig to add JWT filter to filter chain
  - [ ] Configure permitAll() for /api/auth/** endpoints
  - [ ] Configure authenticated() for all other /api/** endpoints
  - [ ] Disable CSRF (stateless JWT authentication)
  - [ ] Set session management to STATELESS
  - [ ] Test: Integration test with valid/invalid tokens

- [ ] Create unit tests for login (AC: 10)
  - [ ] Create AuthServiceLoginTest class
  - [ ] Test successful login returns AuthResponse with token
  - [ ] Test login with non-existent email throws InvalidCredentialsException
  - [ ] Test login with wrong password throws InvalidCredentialsException
  - [ ] Test token contains correct user ID and email
  - [ ] Verify passwordEncoder.matches() called with correct parameters
  - [ ] Test: All unit tests pass with >70% coverage

- [ ] Create integration test for login (AC: 11)
  - [ ] Create AuthControllerLoginIntegrationTest class
  - [ ] Use @SpringBootTest and TestContainers
  - [ ] Test successful login with valid credentials returns 200 OK
  - [ ] Test login with invalid email returns 401 Unauthorized
  - [ ] Test login with invalid password returns 401 Unauthorized
  - [ ] Test response contains valid JWT token
  - [ ] Test token can be used to access protected endpoint
  - [ ] Verify token expiration is 24 hours
  - [ ] Test: All integration tests pass

## Dev Notes

### Requirements Context

**Epic Goal:** Establish technical foundation for Smart Budget App with project structure, development environment, containerization, CI/CD, database, and authentication.

**Story Focus:** Implement user login endpoint that validates credentials, generates JWT tokens, and configures Spring Security to validate tokens on subsequent requests. This completes the authentication system started in Story 1.5.

**Key Architectural Constraints:**
- **JWT Library:** io.jsonwebtoken (jjwt) version 0.12.x [Source: [architecture.md](../architecture.md#Tech-Stack)]
- **Token Algorithm:** HS256 (HMAC with SHA-256) [Source: [tech-spec-epic-1.md](tech-spec-epic-1.md#JWT-Token-Structure)]
- **Token Expiration:** 24 hours [Source: PRD AC 5]
- **Secret Key:** Loaded from environment variable JWT_SECRET, minimum 256 bits for HS256 [Source: [tech-spec-epic-1.md](tech-spec-epic-1.md#Security)]
- **Session Management:** Stateless (no server-side sessions) [Source: [architecture.md](../architecture.md#Security-Architecture)]
- **Error Message:** Generic "Invalid credentials" for both invalid email and password (prevent email enumeration) [Source: PRD AC 4]

### Project Structure Notes

**Expected Backend Files:**
```
backend/
└── src/
    └── main/
        └── java/com/smartbudget/
            ├── config/
            │   ├── SecurityConfig.java           # Update with JWT filter
            │   └── JwtAuthenticationFilter.java  # NEW: JWT filter
            ├── controller/
            │   └── AuthController.java           # Add login endpoint
            ├── dto/
            │   ├── LoginRequest.java             # NEW: Login request DTO
            │   └── AuthResponse.java             # NEW: Auth response DTO
            ├── service/
            │   ├── AuthService.java              # Add login method
            │   └── JwtService.java               # NEW: Token generation/validation
            ├── exception/
            │   ├── InvalidCredentialsException.java  # NEW: Custom exception
            │   └── GlobalExceptionHandler.java       # Add handler for InvalidCredentialsException
            └── repository/
                └── UserRepository.java           # Already has findByEmail()
```

**DTO Structure:**

LoginRequest:
```java
public class LoginRequest {
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
```

AuthResponse:
```java
public class AuthResponse {
    private String accessToken;
    private UserResponse user;

    public AuthResponse(String accessToken, UserResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
```

**JWT Token Payload:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "iat": 1699564800,
  "exp": 1699651200
}
```

**Login Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

**Login Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/auth/login"
}
```

### Learnings from Previous Story

**From Story 1.5: User Registration Backend API (Status: review)**

**Authentication Infrastructure Already in Place:**
- UserRepository with findByEmail() and existsByEmail() methods
- PasswordEncoder bean (BCrypt with work factor 10) in SecurityConfig
- UserResponse DTO for API responses (excludes passwordHash)
- GlobalExceptionHandler with @ControllerAdvice for consistent error responses
- AuthService for authentication business logic
- AuthController with POST /api/auth/register endpoint
- Swagger/OpenAPI configuration with @Tag and @Operation annotations

**Files to Reuse:**
1. **backend/src/main/java/com/smartbudget/repository/UserRepository.java** - Has findByEmail() method needed for login
2. **backend/src/main/java/com/smartbudget/config/SecurityConfig.java** - Has PasswordEncoder bean, needs JWT filter configuration
3. **backend/src/main/java/com/smartbudget/service/AuthService.java** - Add login() method
4. **backend/src/main/java/com/smartbudget/controller/AuthController.java** - Add login endpoint
5. **backend/src/main/java/com/smartbudget/dto/UserResponse.java** - Use in AuthResponse
6. **backend/src/main/java/com/smartbudget/exception/GlobalExceptionHandler.java** - Add InvalidCredentialsException handler

**Key Insight for This Story:**
The authentication foundation from Story 1.5 significantly reduces the scope of this story. We can reuse the UserRepository, PasswordEncoder, and error handling infrastructure. The main additions are JWT token generation/validation and Spring Security filter configuration.

**Testing Best Practices from Story 1.5:**
1. Unit tests: Mock dependencies (UserRepository, PasswordEncoder, JwtService)
2. Integration tests: Use TestContainers with real database
3. Test all validation scenarios (invalid email, blank password)
4. Test both success and failure paths
5. Verify tokens are valid and contain correct claims

[Source: [1-5-user-registration-backend-api.md](1-5-user-registration-backend-api.md#Completion-Notes-List)]

**From Story 1.4: CI/CD Pipeline Setup (Status: review)**

**Testing Infrastructure:**
- JaCoCo code coverage configured with 70% minimum threshold
- Integration tests use TestContainers for PostgreSQL
- GitHub Actions workflow runs tests automatically on PR
- Coverage reports generated in build/reports/jacoco/test/

**Key Insight for This Story:**
All new code must meet 70% coverage threshold or CI build will fail. Ensure comprehensive unit and integration tests for JwtService, login logic, and JWT filter.

[Source: [1-4-ci-cd-pipeline-setup.md](1-4-ci-cd-pipeline-setup.md#Completion-Notes-List)]

### References

- **PRD Epic 1:** [docs/prd/epic-1-foundation-core-infrastructure.md](../prd/epic-1-foundation-core-infrastructure.md#Story-1.6) - Story 1.6 acceptance criteria
- **Tech Spec Epic 1:** [docs/sprint-artifacts/tech-spec-epic-1.md](tech-spec-epic-1.md#JWT-Token-Structure) - JWT structure, login flow, security configuration
- **Architecture:** [docs/architecture.md](../architecture.md#Security-Architecture) - Stateless authentication, JWT strategy
- **Story 1.5:** [docs/sprint-artifacts/1-5-user-registration-backend-api.md](1-5-user-registration-backend-api.md) - Reusable authentication infrastructure
- **Story 1.4:** [docs/sprint-artifacts/1-4-ci-cd-pipeline-setup.md](1-4-ci-cd-pipeline-setup.md) - Testing requirements and coverage thresholds
- **JJWT Docs:** https://github.com/jwtk/jjwt#install - JWT library documentation
- **Spring Security Docs:** https://docs.spring.io/spring-security/reference/servlet/authentication/index.html - Spring Security configuration

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Sonnet 4.5 (claude-sonnet-4-5-20250929)

### Debug Log References

### Completion Notes List

**Implementation Status:** Complete - All acceptance criteria implemented

**Summary:**
Successfully implemented user login endpoint that validates credentials against stored BCrypt hash, generates JWT access tokens, and configures Spring Security to validate tokens on subsequent requests. This completes the authentication system enabling users to securely access protected features.

**Key Implementation Details:**

**1. DTOs Created:**
- **LoginRequest.java**: Login request DTO with email/password validation
  - @Email and @NotBlank validation for email field
  - @NotBlank validation for password field
- **AuthResponse.java**: Authentication response DTO
  - Contains accessToken (JWT) and UserResponse fields

**2. JWT Service:**
- **JwtService.java**: Token generation and validation utility
  - generateToken(User) - Creates JWT with 24-hour expiration
  - extractUserId(String) - Extracts user ID from token
  - extractEmail(String) - Extracts email from token
  - validateToken(String) - Validates token signature and expiration
  - Uses HS256 algorithm for signing
  - Secret key loaded from environment variable (JWT_SECRET)
  - Token payload includes: sub (user ID), email, iat (issued at), exp (expiration)

**3. Service Layer:**
- **AuthService.java**: Updated with login() method
  - Queries user by email using userRepository.findByEmail()
  - Verifies password using passwordEncoder.matches()
  - Throws InvalidCredentialsException for invalid email or password
  - Generates JWT token using jwtService.generateToken()
  - Returns AuthResponse with token and UserResponse
  - Uses @Transactional(readOnly = true) for database query

**4. Controller Layer:**
- **AuthController.java**: Added POST /api/auth/login endpoint
  - Accepts @Valid @RequestBody LoginRequest
  - Returns 200 OK with AuthResponse
  - Includes OpenAPI/Swagger annotations (@Operation, @ApiResponses)

**5. Exception Handling:**
- **InvalidCredentialsException.java**: Custom exception for authentication failures
- **GlobalExceptionHandler.java**: Updated with handler for InvalidCredentialsException
  - Returns 401 Unauthorized with generic "Invalid credentials" message
  - Prevents email enumeration attacks

**6. Spring Security Configuration:**
- **JwtAuthenticationFilter.java**: Custom filter extending OncePerRequestFilter
  - Extracts JWT from Authorization header (Bearer token)
  - Validates token using jwtService.validateToken()
  - Extracts user ID and sets authentication in SecurityContext
  - Allows requests to continue if no token present (permits public endpoints)

- **SecurityConfig.java**: Updated with JWT filter and security rules
  - Disabled CSRF (stateless JWT authentication)
  - Configured permitAll() for /api/auth/** (registration, login)
  - Configured permitAll() for /swagger-ui/**, /v3/api-docs/** (API documentation)
  - Configured authenticated() for all other /api/** endpoints
  - Set session management to STATELESS (no server-side sessions)
  - Added JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter

**7. Testing:**
- **AuthServiceLoginTest.java**: Unit tests for login logic with Mockito
  - Test successful login returns AuthResponse with token
  - Test login with non-existent email throws InvalidCredentialsException
  - Test login with incorrect password throws InvalidCredentialsException
  - Test password verification called with correct arguments
  - Test token generation called with correct user
  - Coverage: 5 test methods covering all service logic paths

- **JwtServiceTest.java**: Unit tests for JWT operations
  - Test token generation creates valid JWT (3 parts)
  - Test extractUserId returns correct user ID
  - Test extractEmail returns correct email
  - Test validateToken with valid token returns true
  - Test validateToken with invalid token returns false
  - Test validateToken with tampered token returns false
  - Test different users generate different tokens
  - Coverage: 7 test methods

- **AuthControllerLoginIntegrationTest.java**: End-to-end tests with TestContainers
  - Test successful login with valid credentials (200 OK)
  - Test login with invalid email returns 401 Unauthorized
  - Test login with invalid password returns 401 Unauthorized
  - Test validation errors (blank email, blank password, invalid format) return 400
  - Test response contains valid JWT token
  - Test token contains correct claims (user ID, email)
  - Test token allows access to protected endpoints (JWT filter works)
  - Test requests without token rejected (401 Unauthorized)
  - Test requests with invalid token rejected (401 Unauthorized)
  - Coverage: 11 test methods covering all scenarios

**8. Dependencies Added:**
- io.jsonwebtoken:jjwt-api:0.12.3 (JWT API)
- io.jsonwebtoken:jjwt-impl:0.12.3 (JWT implementation)
- io.jsonwebtoken:jjwt-jackson:0.12.3 (JWT JSON processing)

**API Documentation:**
- Swagger UI available at: http://localhost:8080/swagger-ui.html
- Login endpoint documented with request/response examples

**Login Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "createdAt": "2025-11-16T10:30:00",
    "updatedAt": "2025-11-16T10:30:00"
  }
}
```

**Login Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2025-11-16T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/auth/login"
}
```

**JWT Token Payload:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "iat": 1699564800,
  "exp": 1699651200
}
```

**Security Features:**
- Generic "Invalid credentials" message prevents email enumeration
- JWT secret loaded from environment variable (not hardcoded)
- BCrypt password verification (work factor 10)
- Stateless authentication (no server-side sessions)
- 24-hour token expiration
- CSRF protection disabled (stateless JWT)

**All Acceptance Criteria Met:**
1. ✓ POST /api/auth/login endpoint created
2. ✓ LoginRequest DTO with validation annotations
3. ✓ User credentials validated against BCrypt hash
4. ✓ Failed login returns 401 with generic "Invalid credentials" message
5. ✓ JWT token generated with user ID, email, iat, exp (24 hours)
6. ✓ JWT signed with secret key from environment variable
7. ✓ Login response includes accessToken and user information
8. ✓ JwtService utility class for generation and validation
9. ✓ Spring Security configured with JWT authentication filter
10. ✓ Unit tests cover success and failure scenarios
11. ✓ Integration test validates full login flow and token generation
12. ✓ API documented with OpenAPI/Swagger annotations

### File List

**Created Files:**
1. backend/src/main/java/com/smartbudget/dto/LoginRequest.java - Login request DTO with validation
2. backend/src/main/java/com/smartbudget/dto/AuthResponse.java - Auth response with token and user
3. backend/src/main/java/com/smartbudget/service/JwtService.java - JWT token generation and validation
4. backend/src/main/java/com/smartbudget/exception/InvalidCredentialsException.java - Custom exception for invalid credentials
5. backend/src/main/java/com/smartbudget/config/JwtAuthenticationFilter.java - JWT authentication filter
6. backend/src/test/java/com/smartbudget/service/AuthServiceLoginTest.java - Unit tests for login logic
7. backend/src/test/java/com/smartbudget/service/JwtServiceTest.java - Unit tests for JWT service
8. backend/src/test/java/com/smartbudget/controller/AuthControllerLoginIntegrationTest.java - Integration tests for login

**Modified Files:**
1. backend/build.gradle - Added JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson)
2. backend/src/main/java/com/smartbudget/service/AuthService.java - Added login() method
3. backend/src/main/java/com/smartbudget/controller/AuthController.java - Added login endpoint
4. backend/src/main/java/com/smartbudget/config/SecurityConfig.java - Added JWT filter and security configuration
5. backend/src/main/java/com/smartbudget/exception/GlobalExceptionHandler.java - Added InvalidCredentialsException handler
