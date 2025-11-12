# Security Architecture

## Authentication Flow

```mermaid
sequenceDiagram
    participant User
    participant Angular as Angular Frontend
    participant API as Spring Boot API
    participant DB as PostgreSQL
    participant JWT as JWT Provider

    Note over User,DB: Registration Flow
    User->>Angular: Enter email & password
    Angular->>API: POST /api/auth/register {email, password}
    API->>API: Validate email format & password strength
    API->>API: Hash password with BCrypt (work factor 10)
    API->>DB: INSERT INTO users (email, password_hash)
    DB-->>API: User created (id, email)
    API->>JWT: Generate JWT token (userId, email, exp: 24h)
    JWT-->>API: JWT token
    API-->>Angular: 201 Created {accessToken, user}
    Angular->>Angular: Store token in localStorage
    Angular-->>User: Redirect to dashboard

    Note over User,DB: Login Flow
    User->>Angular: Enter email & password
    Angular->>API: POST /api/auth/login {email, password}
    API->>DB: SELECT * FROM users WHERE email = ?
    DB-->>API: User record (id, email, password_hash)
    API->>API: Verify password with BCrypt
    alt Valid credentials
        API->>JWT: Generate JWT token (userId, email, exp: 24h)
        JWT-->>API: JWT token
        API-->>Angular: 200 OK {accessToken, user}
        Angular->>Angular: Store token in localStorage
        Angular-->>User: Redirect to dashboard
    else Invalid credentials
        API-->>Angular: 401 Unauthorized {error: "Invalid credentials"}
        Angular-->>User: Display error message
    end

    Note over User,DB: Authenticated API Request
    User->>Angular: Perform action (e.g., view transactions)
    Angular->>Angular: Read token from localStorage
    Angular->>API: GET /api/transactions<br/>Authorization: Bearer <token>
    API->>JWT: Validate token signature & expiration
    JWT-->>API: Token valid, extract userId
    API->>DB: SELECT * FROM transactions WHERE user_id = ?
    DB-->>API: User's transactions
    API-->>Angular: 200 OK {transactions[]}
    Angular-->>User: Display transactions
```

## JWT Token Structure

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-uuid-here",
    "email": "user@example.com",
    "iat": 1699564800,
    "exp": 1699651200
  },
  "signature": "..."
}
```

**Token Claims:**
- `sub` (Subject): User UUID
- `email`: User email address
- `iat` (Issued At): Token generation timestamp
- `exp` (Expiration): Token expiration (24 hours from `iat`)

## Security Measures

**Backend (Spring Boot):**
- **Password Hashing:** BCrypt with work factor 10 (configured in Spring Security)
- **JWT Signing:** HMAC-SHA256 with secret key from environment variable
- **CORS Configuration:** Whitelist frontend origin only (no wildcard `*`)
- **Input Validation:** Bean Validation (`@Valid`, `@NotNull`, `@Email`, etc.) on all request DTOs
- **SQL Injection Prevention:** Parameterized queries via JPA (no string concatenation)
- **Authorization:** All endpoints verify user ownership before returning/modifying data
- **HTTPS Enforcement:** All production traffic over TLS 1.2+
- **Security Headers:** Content-Security-Policy, X-Frame-Options, X-Content-Type-Options

**Frontend (Angular):**
- **Token Storage:** JWT stored in `localStorage` (XSS mitigation via Angular's built-in sanitization)
- **HTTP Interceptor:** Automatically attaches token to all outgoing requests
- **Error Interceptor:** Catches 401/403 responses, logs user out, redirects to login
- **Route Guards:** `AuthGuard` prevents access to protected routes without valid token
- **Input Sanitization:** Angular's DomSanitizer prevents XSS in dynamic content
- **CSP Headers:** Content-Security-Policy header blocks inline scripts
