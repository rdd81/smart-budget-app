# Testing Strategy

## Test Pyramid

```
        E2E Tests (Cypress)
       /                  \
    Integration Tests
   /                      \
Frontend Unit Tests    Backend Unit Tests
(Jasmine/Karma)       (JUnit/Mockito)
```

## Testing Approach

**Frontend Tests (60%+ coverage target):**
- **Component Tests:** Test component logic, user interactions, template bindings
- **Service Tests:** Test HTTP calls, data transformations, business logic
- **Pipe/Directive Tests:** Test custom pipes and directives in isolation
- **Integration Tests:** Test component + service interactions

**Backend Tests (70%+ coverage target):**
- **Unit Tests:** Service layer business logic with mocked repositories
- **Integration Tests:** Repository tests with TestContainers (real PostgreSQL)
- **API Tests:** Controller tests with MockMvc, test request/response handling
- **Security Tests:** Test authentication, authorization, input validation

**E2E Tests (Phase 2):**
- Critical user flows: Login, add transaction, view dashboard, filter transactions
- Cross-browser testing: Chrome, Firefox, Safari
- Mobile responsive testing

---

**Last Updated:** 2025-11-11
**Version:** 1.0
**Owner:** Architect (Winston)
**Status:** Approved for MVP Development

---
