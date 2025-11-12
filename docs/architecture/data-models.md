# Data Models

## User

**Purpose:** Represents a registered user account with authentication credentials and profile information.

**Key Attributes:**
- `id`: UUID - Unique identifier (primary key)
- `email`: String - User's email address (unique, used for login)
- `passwordHash`: String - BCrypt hashed password (never returned in API responses)
- `createdAt`: Timestamp - Account creation date
- `updatedAt`: Timestamp - Last profile update date

### TypeScript Interface

```typescript
export interface User {
  id: string;
  email: string;
  // passwordHash intentionally excluded from frontend interface
  createdAt: Date;
  updatedAt: Date;
}

export interface UserProfile extends User {
  // Future extensions: name, avatar, preferences
}
```

### Relationships

- One-to-many with Transaction (one user has many transactions)
- One-to-many with CategorizationFeedback (one user provides many feedback entries)

## Transaction

**Purpose:** Represents a single financial transaction (income or expense) recorded by a user.

**Key Attributes:**
- `id`: UUID - Unique identifier (primary key)
- `userId`: UUID - Foreign key to User (owner of transaction)
- `amount`: Decimal - Transaction amount (positive for both income and expenses)
- `transactionDate`: Date - Date of the transaction
- `description`: String - User-provided description (max 255 characters)
- `categoryId`: UUID - Foreign key to Category
- `transactionType`: Enum - INCOME or EXPENSE
- `createdAt`: Timestamp - Record creation date
- `updatedAt`: Timestamp - Last modification date

### TypeScript Interface

```typescript
export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export interface Transaction {
  id: string;
  userId: string;
  amount: number;
  transactionDate: Date;
  description: string;
  category: Category; // Nested object
  transactionType: TransactionType;
  createdAt: Date;
  updatedAt: Date;
}

export interface TransactionRequest {
  amount: number;
  transactionDate: string; // ISO 8601 date string
  description: string;
  categoryId: string;
  transactionType: TransactionType;
}
```

### Relationships

- Many-to-one with User (many transactions belong to one user)
- Many-to-one with Category (many transactions have one category)
- One-to-many with CategorizationFeedback (one transaction may have feedback entries)

## Category

**Purpose:** Represents a predefined category for transaction classification (e.g., Rent, Food, Salary).

**Key Attributes:**
- `id`: UUID - Unique identifier (primary key)
- `name`: String - Category name (e.g., "Food", "Rent", "Salary")
- `type`: Enum - INCOME or EXPENSE (determines if category applies to income or expenses)
- `description`: String - Optional description of the category

### TypeScript Interface

```typescript
export enum CategoryType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export interface Category {
  id: string;
  name: string;
  type: CategoryType;
  description?: string;
}
```

### Relationships

- One-to-many with Transaction (one category used by many transactions)
- One-to-many with CategorizationRule (one category targeted by many rules)

## CategorizationRule

**Purpose:** Represents a rule for AI-powered transaction categorization based on keywords and patterns.

**Key Attributes:**
- `id`: UUID - Unique identifier (primary key)
- `keyword`: String - Keyword or regex pattern to match in transaction description
- `categoryId`: UUID - Foreign key to Category (suggested category)
- `transactionType`: Enum - INCOME or EXPENSE (rule applies to this type)
- `confidence`: Decimal - Confidence score (0.0 to 1.0) for this rule
- `isGlobal`: Boolean - True for system-wide rules, false for user-specific rules
- `userId`: UUID - Foreign key to User (null for global rules, set for user-specific rules)

### TypeScript Interface

```typescript
export interface CategorizationRule {
  id: string;
  keyword: string;
  categoryId: string;
  transactionType: TransactionType;
  confidence: number; // 0.0 to 1.0
  isGlobal: boolean;
  userId?: string; // Optional, only for user-specific rules
}
```

### Relationships

- Many-to-one with Category (many rules suggest one category)
- Many-to-one with User (many user-specific rules belong to one user)

## CategorizationFeedback

**Purpose:** Tracks user corrections to AI category suggestions to enable personalized learning.

**Key Attributes:**
- `id`: UUID - Unique identifier (primary key)
- `userId`: UUID - Foreign key to User (who provided feedback)
- `transactionId`: UUID - Foreign key to Transaction (which transaction was corrected)
- `description`: String - Transaction description at time of feedback
- `suggestedCategoryId`: UUID - Category suggested by AI
- `actualCategoryId`: UUID - Category chosen by user (correction)
- `createdAt`: Timestamp - When feedback was recorded

### TypeScript Interface

```typescript
export interface CategorizationFeedback {
  id: string;
  userId: string;
  transactionId: string;
  description: string;
  suggestedCategoryId: string;
  actualCategoryId: string;
  createdAt: Date;
}
```

### Relationships

- Many-to-one with User (many feedback entries from one user)
- Many-to-one with Transaction (feedback references one transaction)
- References two Category records (suggested and actual)
