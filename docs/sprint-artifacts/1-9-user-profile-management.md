# Story 1.9: User Profile Management

Status: in-progress

## Story

As a registered user,
I want to view and update my profile information including email and password,
so that I can maintain accurate account details and change my credentials when needed for security.

## Acceptance Criteria

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

## Tasks / Subtasks

- [ ] Create backend DTOs (AC: 3, 6)
  - [ ] Create UpdateEmailRequest DTO with email validation
  - [ ] Create UpdatePasswordRequest DTO with currentPassword and newPassword
  - [ ] Create UserProfileResponse DTO (id, email, createdAt, updatedAt)
  - [ ] Test: Verify validation annotations work correctly

- [ ] Create ProfileService (AC: 10, 7, 8, 9)
  - [ ] Create ProfileService with @Service annotation
  - [ ] Implement getProfile(UUID userId) returning UserProfileResponse
  - [ ] Implement updateEmail(UUID userId, String newEmail) with uniqueness validation
  - [ ] Implement updatePassword(UUID userId, String currentPassword, String newPassword)
  - [ ] Validate current password before allowing password change
  - [ ] Hash new password with BCrypt before saving
  - [ ] Throw appropriate exceptions for validation failures
  - [ ] Test: Unit tests with mocked UserRepository

- [ ] Create UserController profile endpoints (AC: 1, 2, 3, 6, 21)
  - [ ] Create UserController with @RestController
  - [ ] Implement GET /api/users/profile with @AuthenticationPrincipal
  - [ ] Implement PUT /api/users/profile/email
  - [ ] Implement PUT /api/users/profile/password
  - [ ] Add Swagger/OpenAPI annotations to all endpoints
  - [ ] Add @Valid for request validation
  - [ ] Test: Integration tests with MockMvc and TestContainers

- [ ] Create frontend ProfileComponent (AC: 11, 12, 13, 14, 15, 16, 17, 18)
  - [ ] Generate component: ng generate component profile
  - [ ] Create reactive form for email update
  - [ ] Create reactive form for password update with password matching validator
  - [ ] Display current email and createdAt in read-only fields
  - [ ] Implement updateEmail() method calling AuthService or new ProfileService
  - [ ] Implement updatePassword() method
  - [ ] Add loading states for both forms
  - [ ] Display success/error messages
  - [ ] Style with Tailwind CSS matching login/register components
  - [ ] Add proper labels and ARIA attributes
  - [ ] Test: Component tests with mocked services

- [ ] Update frontend routing (AC: 11)
  - [ ] Add /profile route in app.routes.ts
  - [ ] Protect route with authGuard
  - [ ] Add navigation link to profile in dashboard
  - [ ] Test: Verify route navigation works

- [ ] Integration testing (AC: 19, 20)
  - [ ] Backend: Integration tests for all profile endpoints
  - [ ] Test authentication requirements
  - [ ] Test email uniqueness validation
  - [ ] Test password verification
  - [ ] Test successful updates persist to database
  - [ ] Frontend: Component tests

## Dev Notes

### Requirements Context

**Epic Goal:** Complete Epic 1 - Foundation & Core Infrastructure with user profile management functionality.

**Story Focus:** Allow authenticated users to view and update their profile information (email and password) with proper validation, security checks, and user-friendly UI.

**Key Architectural Constraints:**
- **Authentication:** JWT-based, users can only access/modify their own profile [Source: PRD AC 2]
- **Password Security:** Current password verification required, BCrypt hashing [Source: PRD AC 7, 8]
- **Email Validation:** Format validation and uniqueness check [Source: PRD AC 4, 5]
- **Frontend:** Tailwind CSS, reactive forms, loading states [Source: PRD AC 18, 16]
- **Testing:** Unit and integration tests [Source: PRD AC 19, 20]
- **API Documentation:** Swagger annotations [Source: PRD AC 21]

### Project Structure Notes

**Backend Files:**
```
backend/src/main/java/com/smartbudget/
├── controller/
│   └── UserController.java         # Profile endpoints
├── service/
│   └── ProfileService.java         # Profile business logic
├── dto/
│   ├── request/
│   │   ├── UpdateEmailRequest.java
│   │   └── UpdatePasswordRequest.java
│   └── response/
│       └── UserProfileResponse.java
└── exception/
    └── (use existing exceptions)
```

**Frontend Files:**
```
frontend/src/app/
├── profile/
│   ├── profile.component.ts
│   ├── profile.component.html
│   └── profile.component.css
└── app.routes.ts (update)
```

**API Endpoints:**
```
GET  /api/users/profile           - Get current user profile
PUT  /api/users/profile/email     - Update email
PUT  /api/users/profile/password  - Update password
```

**UserProfileResponse Example:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "createdAt": "2025-11-16T10:00:00Z",
  "updatedAt": "2025-11-16T10:00:00Z"
}
```

### Learnings from Previous Stories

**From Story 1.5: User Registration (Status: review)**
- Email uniqueness validation pattern established
- Password hashing with BCrypt work factor 10
- Validation error handling with @Valid

**From Story 1.6: User Login and JWT Authentication (Status: review)**
- JWT authentication working with @AuthenticationPrincipal
- SecurityConfig properly configured
- Password verification with PasswordEncoder

**From Story 1.7: Frontend Authentication UI (Status: review)**
- Reactive forms pattern with validation
- Loading states and error messages
- Tailwind CSS styling established
- AuthService pattern for API calls

**Key Insight for This Story:**
Build on existing authentication infrastructure. Use @AuthenticationPrincipal to get current user from JWT. Follow established patterns for forms, validation, and error handling from Stories 1.5-1.7.

### References

- **PRD Epic 1:** [docs/prd/epic-1-foundation-core-infrastructure.md](../prd/epic-1-foundation-core-infrastructure.md#Story-1.9) - Story 1.9 acceptance criteria
- **Tech Spec Epic 1:** [docs/sprint-artifacts/tech-spec-epic-1.md](tech-spec-epic-1.md) - Architecture patterns
- **Story 1.5:** [docs/sprint-artifacts/1-5-user-registration-backend-api.md](1-5-user-registration-backend-api.md) - Email validation pattern
- **Story 1.6:** [docs/sprint-artifacts/1-6-user-login-and-jwt-authentication.md](1-6-user-login-and-jwt-authentication.md) - JWT authentication
- **Story 1.7:** [docs/sprint-artifacts/1-7-frontend-authentication-ui-and-state-management.md](1-7-frontend-authentication-ui-and-state-management.md) - Frontend patterns
- **Spring Security:** https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
- **Angular Reactive Forms:** https://angular.dev/guide/forms

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

Claude Sonnet 4.5 (claude-sonnet-4-5-20250929)

### Debug Log References

### Completion Notes List

### File List
