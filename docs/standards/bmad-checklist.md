# BMAD Standards Checklist

This checklist ensures all development work follows BMAD (BMad Method) standards for AI-assisted development. Use this checklist for code reviews and before marking stories as complete.

## Table of Contents

- [Code Quality Standards](#code-quality-standards)
- [Testing Standards](#testing-standards)
- [Documentation Standards](#documentation-standards)
- [Security Standards](#security-standards)
- [Git and Version Control](#git-and-version-control)
- [Code Review Checklist](#code-review-checklist)

## Code Quality Standards

### Frontend (Angular/TypeScript)

- [ ] All components use Angular 17+ standalone component pattern
- [ ] TypeScript strict mode enabled and no `any` types without justification
- [ ] Components follow single responsibility principle
- [ ] Services are provided in root or component level appropriately
- [ ] Reactive forms used instead of template-driven forms
- [ ] RxJS observables properly subscribed and unsubscribed (use async pipe or takeUntil)
- [ ] No memory leaks (subscriptions cleaned up in ngOnDestroy)
- [ ] Proper error handling in HTTP requests with user-friendly messages
- [ ] Loading states implemented for async operations
- [ ] Accessibility standards met (WCAG 2.1 Level AA)
- [ ] All forms have proper labels and ARIA attributes
- [ ] Code follows Angular style guide conventions
- [ ] ESLint passes with no errors or warnings
- [ ] No console.log statements in production code
- [ ] Components are modular and reusable where appropriate

### Backend (Spring Boot/Java)

- [ ] Code follows layered architecture (Controller → Service → Repository)
- [ ] Controllers only handle HTTP concerns, no business logic
- [ ] Services contain business logic and are @Transactional where needed
- [ ] Repositories use Spring Data JPA, no raw JDBC
- [ ] All API endpoints have proper validation (@Valid, validation annotations)
- [ ] DTOs used for request/response (never expose entities directly)
- [ ] Proper exception handling with @ControllerAdvice
- [ ] No sensitive data in exception messages or logs
- [ ] SQL injection prevention (use parameterized queries only)
- [ ] No hardcoded credentials or secrets
- [ ] Environment variables used for all configuration
- [ ] Database connections properly pooled (HikariCP)
- [ ] Transactions properly managed (@Transactional on service methods)
- [ ] Code formatted consistently (follow Spring Boot conventions)
- [ ] No unused imports or variables
- [ ] Proper logging levels (DEBUG, INFO, WARN, ERROR)

### General Code Quality

- [ ] Code is self-documenting with clear variable and method names
- [ ] Complex logic has explanatory comments
- [ ] No commented-out code (use version control instead)
- [ ] No dead code or unused methods
- [ ] Magic numbers replaced with named constants
- [ ] Methods are small and focused (< 50 lines)
- [ ] Classes have single responsibility
- [ ] No duplicated code (DRY principle)
- [ ] Error messages are user-friendly and actionable
- [ ] All edge cases handled appropriately

## Testing Standards

### Frontend Testing

- [ ] Unit tests for all components (minimum 60% coverage)
- [ ] Unit tests for all services
- [ ] Test coverage report generated
- [ ] Tests use Jasmine/Karma
- [ ] Mocks used for HTTP calls and external dependencies
- [ ] Tests follow AAA pattern (Arrange, Act, Assert)
- [ ] Tests are independent and can run in any order
- [ ] Tests have descriptive names (should/it statements)
- [ ] No skipped tests (unless documented with reason)
- [ ] All tests pass before committing

### Backend Testing

- [ ] Unit tests for all service methods (minimum 70% coverage)
- [ ] Integration tests for controller endpoints
- [ ] Repository tests use Testcontainers with real database
- [ ] Test coverage report generated with JaCoCo
- [ ] Tests use JUnit 5, Mockito, and AssertJ
- [ ] Tests follow Given-When-Then pattern
- [ ] Happy path and error scenarios both tested
- [ ] Edge cases and boundary conditions tested
- [ ] Database transactions properly tested
- [ ] Security rules tested (authorization, authentication)
- [ ] API validation tested (400 Bad Request scenarios)
- [ ] No skipped tests (unless documented with reason)
- [ ] All tests pass before committing

### Integration Testing

- [ ] End-to-end user flows tested
- [ ] Authentication and authorization flows tested
- [ ] Database migrations tested
- [ ] API contract tests (request/response formats)
- [ ] Error handling tested (4xx, 5xx responses)

## Documentation Standards

### Code Documentation

- [ ] All public methods have Javadoc/TSDoc comments
- [ ] Complex algorithms explained with inline comments
- [ ] README.md exists in frontend and backend directories
- [ ] API endpoints documented with OpenAPI/Swagger
- [ ] Environment variables documented
- [ ] Setup instructions documented and tested

### Story Documentation

- [ ] Story file exists in docs/sprint-artifacts/
- [ ] All acceptance criteria documented
- [ ] Tasks/subtasks listed
- [ ] Dev notes include requirements context
- [ ] References to PRD and tech spec included
- [ ] Completion notes added when story is done
- [ ] File list of created/modified files included
- [ ] Sprint status updated (backlog → in-progress → review → done)

### Architecture Documentation

- [ ] Major architectural decisions documented
- [ ] Database schema documented (ERD if applicable)
- [ ] API endpoints documented
- [ ] Deployment process documented
- [ ] Environment configuration documented

## Security Standards

### Authentication and Authorization

- [ ] JWT tokens properly signed and validated
- [ ] Passwords hashed with BCrypt (work factor 10+)
- [ ] No passwords or secrets in logs
- [ ] No password enumeration (generic error messages)
- [ ] JWT tokens expire after reasonable time (24 hours)
- [ ] Auth endpoints exempt from authentication
- [ ] Protected endpoints require valid JWT
- [ ] User can only access their own data

### Input Validation

- [ ] All user input validated on backend
- [ ] SQL injection prevention (parameterized queries only)
- [ ] XSS prevention (input sanitization, output encoding)
- [ ] CSRF protection disabled for stateless API (using JWT)
- [ ] File upload validation (if applicable)
- [ ] Request size limits enforced
- [ ] Rate limiting considered (if applicable)

### Data Protection

- [ ] Sensitive data encrypted in transit (HTTPS)
- [ ] Database credentials stored in environment variables
- [ ] JWT secret stored in environment variable (not hardcoded)
- [ ] No sensitive data in client-side storage (except JWT token)
- [ ] Proper CORS configuration (whitelist origins in production)
- [ ] Security headers configured (X-Frame-Options, CSP, etc.)
- [ ] Database backups configured
- [ ] Personal data handling compliant with regulations

### Container Security

- [ ] Containers run as non-root user
- [ ] Minimal base images used (Alpine)
- [ ] Multi-stage builds to reduce image size
- [ ] No secrets in Docker images or layers
- [ ] Health checks configured
- [ ] Resource limits set (CPU, memory)

## Git and Version Control

### Commit Standards

- [ ] Commits are atomic (single logical change)
- [ ] Commit messages follow convention:
  - Subject line: imperative mood, 50 chars max
  - Body: explains what and why, not how
  - Example: "Add health check endpoint for monitoring"
- [ ] Commits include co-authoring with AI:
  ```
  Co-Authored-By: Claude <noreply@anthropic.com>
  ```
- [ ] No merge commits in feature branch history (rebase preferred)
- [ ] No commits directly to main branch (use PRs)

### Branch Standards

- [ ] Feature branches named descriptively (story-1-8-deployment-config)
- [ ] Branches created from latest main
- [ ] Branches deleted after merge
- [ ] No long-lived feature branches (merge frequently)

### Pull Request Standards

- [ ] PR title describes the change
- [ ] PR description includes:
  - Summary of changes
  - Links to story/issue
  - Test plan
  - Screenshots (if UI changes)
- [ ] PR targets correct base branch (usually main)
- [ ] All CI checks pass (tests, linting, coverage)
- [ ] At least one approval before merge
- [ ] Conflicts resolved before merge
- [ ] Branch up-to-date with base branch

## Code Review Checklist

### Functionality

- [ ] Code meets all acceptance criteria in story
- [ ] Feature works as expected (manual testing done)
- [ ] Edge cases handled correctly
- [ ] Error scenarios handled gracefully
- [ ] No regressions introduced (existing features still work)

### Code Quality

- [ ] Code is readable and maintainable
- [ ] No code smells (long methods, god classes, etc.)
- [ ] Follows DRY principle (no duplication)
- [ ] Follows SOLID principles
- [ ] Appropriate design patterns used
- [ ] Performance optimized (no unnecessary loops, queries, etc.)

### Testing

- [ ] All new code has tests
- [ ] Tests are meaningful (not just for coverage)
- [ ] Test coverage meets thresholds (60% frontend, 70% backend)
- [ ] All tests pass

### Security

- [ ] No security vulnerabilities introduced
- [ ] Input validation present
- [ ] No hardcoded secrets
- [ ] Authorization checks present
- [ ] Security best practices followed

### Documentation

- [ ] Code is self-documenting or has comments
- [ ] API changes documented
- [ ] README updated if needed
- [ ] Story documentation updated

### CI/CD

- [ ] All pipeline checks pass
- [ ] Build artifacts generated successfully
- [ ] Docker images build successfully
- [ ] No linter warnings or errors

## Story Completion Checklist

Before marking a story as "review":

- [ ] All acceptance criteria met
- [ ] All tasks/subtasks completed
- [ ] Tests written and passing
- [ ] Coverage thresholds met
- [ ] Code review completed
- [ ] Documentation updated
- [ ] Story file updated with completion notes
- [ ] Sprint status updated to "review"

Before marking a story as "done":

- [ ] SM code review completed
- [ ] All review feedback addressed
- [ ] Merged to main branch
- [ ] Deployed to staging (if applicable)
- [ ] Manual testing in staging (if applicable)
- [ ] Sprint status updated to "done"

## Definition of Done (DoD)

A story is considered "done" when:

1. **Functional:**
   - All acceptance criteria met
   - Feature works as designed
   - No known bugs

2. **Tested:**
   - Unit tests written and passing
   - Integration tests written and passing
   - Coverage thresholds met
   - Manual testing completed

3. **Reviewed:**
   - Code review completed
   - All feedback addressed
   - Approved by SM

4. **Documented:**
   - Story documentation complete
   - Code comments added
   - API documentation updated
   - README updated if needed

5. **Deployed:**
   - Merged to main branch
   - CI/CD pipeline passes
   - Deployed to staging (for sprint-end stories)

6. **Tracked:**
   - Sprint status updated
   - Story file includes completion notes
   - Learnings documented for next story

## References

- **Angular Style Guide:** https://angular.dev/style-guide
- **Spring Boot Best Practices:** https://spring.io/guides
- **WCAG 2.1 Guidelines:** https://www.w3.org/WAI/WCAG21/quickref/
- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **Git Commit Message Guidelines:** https://www.conventionalcommits.org/
- **JUnit 5 Best Practices:** https://junit.org/junit5/docs/current/user-guide/
- **Docker Security Best Practices:** https://docs.docker.com/develop/security-best-practices/

## Continuous Improvement

This checklist is a living document. Update it based on:
- Retrospective findings
- New security vulnerabilities discovered
- New best practices adopted
- Team feedback
- BMAD methodology updates

**Last Updated:** 2025-11-16
**Version:** 1.0
**Owner:** Project Team
