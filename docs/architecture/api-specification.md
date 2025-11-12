# API Specification

The Smart Budget App uses **REST API** following OpenAPI 3.0 specification.

## Base URL

- **Development:** `http://localhost:8080/api`
- **Staging:** `https://staging-api.smartbudgetapp.com/api`
- **Production:** `https://api.smartbudgetapp.com/api`

## Authentication

All endpoints except `/auth/login` and `/auth/register` require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

## Endpoint Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user account | No |
| POST | `/auth/login` | Login and receive JWT token | No |
| GET | `/transactions` | List all user transactions (filtered, sorted, paginated) | Yes |
| GET | `/transactions/{id}` | Get single transaction by ID | Yes |
| POST | `/transactions` | Create new transaction | Yes |
| PUT | `/transactions/{id}` | Update existing transaction | Yes |
| DELETE | `/transactions/{id}` | Delete transaction | Yes |
| POST | `/transactions/bulk-categorize` | Apply AI categorization to multiple transactions | Yes |
| GET | `/categories` | List all categories | Yes |
| POST | `/categorization/suggest` | Get AI category suggestion for transaction description | Yes |
| GET | `/analytics/summary` | Get financial summary (income, expenses, balance) | Yes |
| GET | `/analytics/category-breakdown` | Get spending breakdown by category | Yes |
| GET | `/analytics/trends` | Get spending trends over time | Yes |
| GET | `/users/profile` | Get current user profile | Yes |
| PUT | `/users/profile` | Update user profile | Yes |
| PUT | `/users/password` | Change user password | Yes |

## OpenAPI 3.0 Specification (Excerpt)

```yaml
openapi: 3.0.0
info:
  title: Smart Budget App API
  version: 1.0.0
  description: REST API for Smart Budget App - personal finance management
servers:
  - url: http://localhost:8080/api
    description: Development server
  - url: https://api.smartbudgetapp.com/api
    description: Production server

paths:
  /auth/register:
    post:
      tags: [Authentication]
      summary: Register new user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [email, password]
              properties:
                email:
                  type: string
                  format: email
                  example: user@example.com
                password:
                  type: string
                  format: password
                  minLength: 8
                  example: SecurePass123
      responses:
        '201':
          description: User created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuthResponse'
        '400':
          description: Validation error (invalid email, weak password, email already exists)

  /auth/login:
    post:
      tags: [Authentication]
      summary: Login and receive JWT token
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required: [email, password]
              properties:
                email:
                  type: string
                  format: email
                password:
                  type: string
                  format: password
      responses:
        '200':
          description: Login successful
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AuthResponse'
        '401':
          description: Invalid credentials

  /transactions:
    get:
      tags: [Transactions]
      summary: List user transactions
      security:
        - BearerAuth: []
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
        - name: sortBy
          in: query
          schema:
            type: string
            enum: [transactionDate, amount, createdAt]
            default: transactionDate
        - name: sortDirection
          in: query
          schema:
            type: string
            enum: [asc, desc]
            default: desc
        - name: dateFrom
          in: query
          schema:
            type: string
            format: date
        - name: dateTo
          in: query
          schema:
            type: string
            format: date
        - name: categoryId
          in: query
          schema:
            type: string
            format: uuid
        - name: transactionType
          in: query
          schema:
            type: string
            enum: [INCOME, EXPENSE]
      responses:
        '200':
          description: List of transactions
          content:
            application/json:
              schema:
                type: object
                properties:
                  content:
                    type: array
                    items:
                      $ref: '#/components/schemas/TransactionResponse'
                  totalElements:
                    type: integer
                  totalPages:
                    type: integer
                  page:
                    type: integer
                  size:
                    type: integer

    post:
      tags: [Transactions]
      summary: Create new transaction
      security:
        - BearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TransactionRequest'
      responses:
        '201':
          description: Transaction created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TransactionResponse'
        '400':
          description: Validation error

  /transactions/{id}:
    get:
      tags: [Transactions]
      summary: Get transaction by ID
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '200':
          description: Transaction details
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TransactionResponse'
        '404':
          description: Transaction not found
        '403':
          description: Unauthorized (transaction belongs to another user)

    put:
      tags: [Transactions]
      summary: Update transaction
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
            format: uuid
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TransactionRequest'
      responses:
        '200':
          description: Transaction updated
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TransactionResponse'

    delete:
      tags: [Transactions]
      summary: Delete transaction
      security:
        - BearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: string
            format: uuid
      responses:
        '204':
          description: Transaction deleted successfully
        '404':
          description: Transaction not found

components:
  schemas:
    AuthResponse:
      type: object
      properties:
        accessToken:
          type: string
          description: JWT access token (valid for 24 hours)
        user:
          type: object
          properties:
            id:
              type: string
              format: uuid
            email:
              type: string
              format: email

    TransactionRequest:
      type: object
      required: [amount, transactionDate, description, categoryId, transactionType]
      properties:
        amount:
          type: number
          format: decimal
          minimum: 0.01
          example: 49.99
        transactionDate:
          type: string
          format: date
          example: "2025-01-15"
        description:
          type: string
          maxLength: 255
          example: "Grocery shopping at Whole Foods"
        categoryId:
          type: string
          format: uuid
        transactionType:
          type: string
          enum: [INCOME, EXPENSE]
          example: EXPENSE
        suggestedCategoryId:
          type: string
          format: uuid
          description: Optional - for feedback tracking if AI suggested different category

    TransactionResponse:
      type: object
      properties:
        id:
          type: string
          format: uuid
        amount:
          type: number
          format: decimal
        transactionDate:
          type: string
          format: date
        description:
          type: string
        category:
          $ref: '#/components/schemas/Category'
        transactionType:
          type: string
          enum: [INCOME, EXPENSE]
        createdAt:
          type: string
          format: date-time
        updatedAt:
          type: string
          format: date-time

    Category:
      type: object
      properties:
        id:
          type: string
          format: uuid
        name:
          type: string
          example: "Food"
        type:
          type: string
          enum: [INCOME, EXPENSE]
        description:
          type: string

  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```

**Full OpenAPI specification:** Available at `/api/swagger-ui.html` (Springdoc OpenAPI UI)
