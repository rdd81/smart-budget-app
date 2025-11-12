# Coding Standards

This document defines **CRITICAL** coding rules for the Smart Budget App. These standards prevent common mistakes, ensure security, and maintain consistency. All code must follow these rules.

> **Note:** This document contains MINIMAL but ESSENTIAL standards. For comprehensive style guides, refer to [Angular Style Guide](https://angular.io/guide/styleguide) and [Spring Boot Best Practices](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html).

## Universal Rules (Frontend & Backend)

### 1. **Never Commit Secrets**
- ❌ **FORBIDDEN:** Hardcoded API keys, passwords, tokens, or connection strings in code
- ✅ **REQUIRED:** All secrets in environment variables or external configuration
- ✅ **REQUIRED:** Use `.env.example` with placeholder values, never `.env` file

**Example:**
```typescript
// ❌ WRONG
const API_KEY = 'sk-prod-abc123';

// ✅ CORRECT
const API_KEY = process.env.API_KEY;
```

### 2. **All Environment Access Through Configuration Objects**
- ❌ **FORBIDDEN:** Direct access to `process.env` or `System.getenv()` throughout codebase
- ✅ **REQUIRED:** Centralized configuration objects/services

**Frontend Example:**
```typescript
// ❌ WRONG (scattered throughout app)
fetch(process.env.API_URL + '/transactions');

// ✅ CORRECT (centralized in environment.ts)
import { environment } from '@environments/environment';
fetch(environment.apiUrl + '/transactions');
```

**Backend Example:**
```java
// ❌ WRONG
String dbUrl = System.getenv("DATABASE_URL");

// ✅ CORRECT (use @Value or @ConfigurationProperties)
@Value("${spring.datasource.url}")
private String databaseUrl;
```

### 3. **Input Validation on All External Data**
- ✅ **REQUIRED:** Validate ALL user input, API requests, and external data
- ✅ **REQUIRED:** Use framework validation (@Valid, Validators, etc.)
- ✅ **REQUIRED:** Sanitize input to prevent XSS, SQL injection, command injection

### 4. **Error Messages Must Not Leak Sensitive Information**
- ❌ **FORBIDDEN:** Stack traces, database errors, or internal paths in production error messages
- ✅ **REQUIRED:** Generic user-friendly messages in frontend
- ✅ **REQUIRED:** Detailed errors logged securely on backend only

**Example:**
```typescript
// ❌ WRONG
catch (error) {
  alert(`Database query failed: ${error.stack}`);
}

// ✅ CORRECT
catch (error) {
  this.errorService.handleError(error); // Logs details internally
  this.toastService.error('Unable to load data. Please try again.');
}
```

### 5. **TypeScript Strict Mode Required**
- ✅ **REQUIRED:** `"strict": true` in tsconfig.json (frontend)
- ✅ **REQUIRED:** No `any` types without explicit reason and comment
- ✅ **REQUIRED:** Explicit return types for all functions

```typescript
// ❌ WRONG
function getTotal(transactions) {
  return transactions.reduce((sum, t) => sum + t.amount, 0);
}

// ✅ CORRECT
function getTotal(transactions: Transaction[]): number {
  return transactions.reduce((sum, t) => sum + t.amount, 0);
}
```

## Frontend (Angular) Standards

### 6. **Core Module Imported Once Only**
- ❌ **FORBIDDEN:** Importing `CoreModule` in feature modules
- ✅ **REQUIRED:** Import `CoreModule` ONLY in `AppModule`
- **Rationale:** Prevents multiple instances of singleton services

### 7. **Services Must NOT Be Provided in Components**
- ❌ **FORBIDDEN:** `@Component({ providers: [SomeService] })`
- ✅ **REQUIRED:** Services provided at module level or `providedIn: 'root'`
- **Exception:** Intentional component-scoped services (rare, document why)

### 8. **All HTTP Calls Through Service Layer**
- ❌ **FORBIDDEN:** Direct `HttpClient` calls in components
- ✅ **REQUIRED:** All API calls via dedicated service classes

**Example:**
```typescript
// ❌ WRONG (in component)
this.http.get('/api/transactions').subscribe(...);

// ✅ CORRECT (in component)
this.transactionService.getTransactions().subscribe(...);
```

### 9. **Unsubscribe from Observables**
- ✅ **REQUIRED:** Unsubscribe from long-lived observables to prevent memory leaks
- ✅ **RECOMMENDED:** Use `takeUntil()`, `async` pipe, or `takeUntilDestroyed()`

**Example:**
```typescript
// ✅ CORRECT
export class MyComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.dataService.getData()
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => this.data = data);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
```

### 10. **Component State Must Be Immutable**
- ❌ **FORBIDDEN:** Mutating arrays or objects directly
- ✅ **REQUIRED:** Use immutable update patterns

**Example:**
```typescript
// ❌ WRONG
this.transactions.push(newTransaction);

// ✅ CORRECT
this.transactions = [...this.transactions, newTransaction];
```

### 11. **Form Validation Required**
- ✅ **REQUIRED:** All forms must have validation before submission
- ✅ **REQUIRED:** Disable submit button until form valid
- ✅ **REQUIRED:** Display validation errors inline

### 12. **Accessibility: Labels for All Inputs**
- ✅ **REQUIRED:** Every `<input>`, `<select>`, `<textarea>` must have associated `<label>` or `aria-label`
- ✅ **REQUIRED:** Maintain 4.5:1 color contrast for text
- ✅ **REQUIRED:** All interactive elements keyboard accessible

## Backend (Spring Boot / Java) Standards

### 13. **Layered Architecture Strictly Enforced**
- ❌ **FORBIDDEN:** Controllers calling Repositories directly
- ✅ **REQUIRED:** Controller → Service → Repository flow
- ❌ **FORBIDDEN:** Business logic in Controllers or Repositories

**Example:**
```java
// ❌ WRONG
@RestController
public class TransactionController {
    @Autowired private TransactionRepository repository;

    @GetMapping("/transactions")
    public List<Transaction> getAll() {
        return repository.findAll(); // Business logic missing!
    }
}

// ✅ CORRECT
@RestController
public class TransactionController {
    @Autowired private TransactionService service;

    @GetMapping("/transactions")
    public List<TransactionResponse> getAll(@AuthenticationPrincipal User user) {
        return service.getUserTransactions(user.getId());
    }
}
```

### 14. **All Request DTOs Must Be Validated**
- ✅ **REQUIRED:** Use `@Valid` on all request body parameters
- ✅ **REQUIRED:** Add validation annotations (@NotNull, @NotBlank, @Email, @Size, etc.)

**Example:**
```java
// ✅ CORRECT
@PostMapping("/transactions")
public ResponseEntity<TransactionResponse> create(
    @Valid @RequestBody TransactionRequest request,  // @Valid is REQUIRED
    @AuthenticationPrincipal User user
) {
    return ResponseEntity.ok(service.createTransaction(request, user));
}

// Request DTO
public class TransactionRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description too long")
    private String description;
}
```

### 15. **User Authorization Required on All Endpoints**
- ❌ **FORBIDDEN:** Endpoints without authentication (except /auth/login, /auth/register)
- ✅ **REQUIRED:** Verify user can only access their own data
- ✅ **REQUIRED:** Check ownership before modify/delete operations

**Example:**
```java
// ✅ CORRECT
@GetMapping("/transactions/{id}")
public TransactionResponse getById(
    @PathVariable UUID id,
    @AuthenticationPrincipal User user
) {
    Transaction transaction = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    // CRITICAL: Verify ownership
    if (!transaction.getUserId().equals(user.getId())) {
        throw new UnauthorizedException("Cannot access other user's data");
    }

    return mapper.toResponse(transaction);
}
```

### 16. **Never Use Raw SQL Concatenation**
- ❌ **FORBIDDEN:** String concatenation for SQL queries
- ✅ **REQUIRED:** Parameterized queries via JPA/JPQL or @Query with named parameters

**Example:**
```java
// ❌ WRONG (SQL Injection vulnerability!)
String sql = "SELECT * FROM transactions WHERE user_id = '" + userId + "'";

// ✅ CORRECT
@Query("SELECT t FROM Transaction t WHERE t.userId = :userId")
List<Transaction> findByUserId(@Param("userId") UUID userId);
```

### 17. **Password Handling**
- ❌ **FORBIDDEN:** Storing plain text passwords
- ✅ **REQUIRED:** BCrypt with work factor 10+ (Spring Security default)
- ❌ **FORBIDDEN:** Logging passwords or sensitive data
- ❌ **FORBIDDEN:** Returning passwords in API responses

**Example:**
```java
// ✅ CORRECT
@Service
public class AuthService {
    @Autowired private PasswordEncoder passwordEncoder; // BCrypt

    public void registerUser(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), hashedPassword);
        repository.save(user);
    }
}
```

### 18. **Transactions for Multi-Step Operations**
- ✅ **REQUIRED:** Use `@Transactional` for operations that modify multiple entities
- ✅ **REQUIRED:** Place `@Transactional` on Service layer methods, not Controllers

**Example:**
```java
// ✅ CORRECT
@Service
public class TransactionService {

    @Transactional
    public void deleteTransaction(UUID transactionId, UUID userId) {
        Transaction transaction = repository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));

        // Verify ownership
        if (!transaction.getUserId().equals(userId)) {
            throw new UnauthorizedException("Cannot delete");
        }

        // Multi-step operation in single transaction
        feedbackRepository.deleteByTransactionId(transactionId);
        repository.delete(transaction);
    }
}
```

### 19. **Global Exception Handling Required**
- ✅ **REQUIRED:** Use `@ControllerAdvice` for centralized exception handling
- ✅ **REQUIRED:** Return consistent error response format

**Example:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }
}
```

### 20. **Lombok Usage Guidelines**
- ✅ **ALLOWED:** `@Data`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`
- ❌ **FORBIDDEN:** `@ToString` on entities with circular references
- ❌ **FORBIDDEN:** `@EqualsAndHashCode` on JPA entities (use manual implementation)

## Database (Flyway Migrations)

### 21. **Never Modify Existing Migrations**
- ❌ **FORBIDDEN:** Editing existing `V{version}__*.sql` files after they're committed
- ✅ **REQUIRED:** Create new migration file for schema changes

### 22. **Migration Naming Convention**
- ✅ **REQUIRED:** `V{version}__{description}.sql` (e.g., `V1__initial_schema.sql`)
- ✅ **REQUIRED:** Sequential version numbers
- ✅ **REQUIRED:** Descriptive names (snake_case)

### 23. **Always Include Rollback Strategy**
- ✅ **REQUIRED:** Document how to undo migration in comments
- ✅ **RECOMMENDED:** Create corresponding `U{version}__{description}.sql` for complex changes

## Testing Standards

### 24. **Test Naming Convention**
- ✅ **REQUIRED:** Test methods clearly describe what they test

**Example:**
```java
// ✅ CORRECT
@Test
void createTransaction_WithValidData_ReturnsCreatedTransaction() {
    // Arrange, Act, Assert
}

@Test
void createTransaction_WithInvalidAmount_ThrowsValidationException() {
    // Test
}
```

### 25. **Unit Tests Must Be Isolated**
- ✅ **REQUIRED:** Mock external dependencies (no real HTTP calls, no real database)
- ✅ **REQUIRED:** Tests must pass regardless of execution order
- ❌ **FORBIDDEN:** Shared mutable state between tests

### 26. **Integration Tests Use TestContainers**
- ✅ **REQUIRED:** Repository tests use TestContainers for real PostgreSQL
- ❌ **FORBIDDEN:** H2 in-memory database for integration tests (behavior differs from PostgreSQL)

## Git Commit Standards

### 27. **Conventional Commits Format**
- ✅ **REQUIRED:** `<type>(<scope>): <description>`
- **Types:** feat, fix, docs, style, refactor, test, chore
- **Example:** `feat(transactions): add category filter to transaction list`

### 28. **Commit Message Guidelines**
- ✅ **REQUIRED:** Present tense ("add feature" not "added feature")
- ✅ **REQUIRED:** Lowercase (except proper nouns)
- ✅ **REQUIRED:** No period at end of subject line
- ✅ **REQUIRED:** Body explains "why", not "what"

## Code Review Checklist

Before submitting PR, verify:

- [ ] No secrets or sensitive data committed
- [ ] All new code follows layered architecture
- [ ] Input validation on all user-facing endpoints
- [ ] Authorization checks where applicable
- [ ] Tests written for new functionality (70% backend, 60% frontend coverage)
- [ ] No console.log or System.out.println in committed code (use proper logging)
- [ ] TypeScript strict mode violations resolved
- [ ] Accessibility requirements met (labels, contrast, keyboard nav)
- [ ] Error handling implemented
- [ ] Documentation/comments for complex logic

## When in Doubt

1. **Security:** If unsure, ask or err on the side of more security
2. **Performance:** Optimize after measuring, not before (avoid premature optimization)
3. **Simplicity:** Choose the simplest solution that solves the problem
4. **Consistency:** Follow existing patterns in the codebase

---

**Last Updated:** 2025-11-11
**Version:** 1.0
**Owner:** Architect (Winston)
**Status:** Approved - All code must comply
