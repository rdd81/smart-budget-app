# Epic 2: Transaction Management

**Epic Goal:**
Implement the complete transaction management system enabling users to create, view, edit, and delete financial transactions with category assignment. This epic delivers the core data entry and management functionality that forms the foundation of the budgeting experience, allowing users to build their financial dataset for analysis.

## Story 2.1: Backend Transaction CRUD API

As a developer,
I want RESTful API endpoints for transaction management,
so that the frontend can perform all transaction operations securely with proper validation and authorization.

**Acceptance Criteria:**

1. GET /api/transactions endpoint returns paginated list of transactions for authenticated user (filtered by user_id from JWT)
2. GET /api/transactions/{id} endpoint returns single transaction if owned by authenticated user, 404 if not found, 403 if owned by different user
3. POST /api/transactions endpoint creates new transaction with request body: amount, transactionDate, description, categoryId, transactionType (INCOME/EXPENSE)
4. PUT /api/transactions/{id} endpoint updates existing transaction with same fields as POST, validates ownership
5. DELETE /api/transactions/{id} endpoint soft-deletes or hard-deletes transaction, validates ownership
6. TransactionRequest DTO created with validation: amount required and positive, transactionDate required and not future, description max 255 characters, categoryId must be valid existing category, transactionType required enum
7. TransactionResponse DTO includes: id, amount, transactionDate, description, category (nested object with id and name), transactionType, createdAt, updatedAt
8. Service layer (TransactionService) implements business logic with proper authorization checks (user can only access own transactions)
9. Repository queries filter by user_id automatically to prevent data leakage
10. GET /api/transactions supports query parameters: page, size, sortBy (date, amount), sortDirection (asc, desc)
11. All endpoints return appropriate HTTP status codes and error messages
12. Unit tests cover TransactionService logic with >70% coverage
13. Integration tests validate end-to-end flows including authorization checks
14. API documented with OpenAPI/Swagger annotations

## Story 2.2: Category Management API

As a developer,
I want API endpoints to retrieve available categories,
so that the frontend can display category options and validate category assignments.

**Acceptance Criteria:**

1. GET /api/categories endpoint returns list of all available categories (no pagination needed, small dataset)
2. CategoryResponse DTO includes: id, name, type (INCOME/EXPENSE), description
3. Categories are system-wide (not user-specific) - all users see same category list
4. Response includes categories seeded during database migration: Salary, Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Savings, Investments, Other
5. Categories separated by type: income categories (Salary, Savings, Investments, Other) and expense categories (Rent, Transport, Food, Entertainment, Utilities, Healthcare, Shopping, Other)
6. CategoryService and CategoryRepository created following layered architecture
7. Endpoint does not require authentication (categories are public reference data) - ALTERNATIVE: require authentication for consistency
8. Response cached appropriately (categories rarely change)
9. Unit and integration tests validate category retrieval
10. API documented with OpenAPI/Swagger annotations

## Story 2.3: Transaction List View Component

As a user,
I want to view a list of all my transactions in a clear, organized table,
so that I can see my complete financial history at a glance and quickly find specific transactions.

**Acceptance Criteria:**

1. TransactionListComponent created displaying transactions in a responsive table/card layout
2. Table columns show: Date, Description, Category, Amount, Type (Income/Expense), Actions (Edit/Delete icons)
3. Amount displayed with currency formatting (e.g., $1,234.56) with color coding (green for income, red for expense)
4. Date formatted in user-friendly format (e.g., "Jan 15, 2025" or "01/15/2025")
5. Transactions sorted by date descending (most recent first) by default
6. Column headers clickable for sorting (toggle asc/desc) - at minimum sort by Date and Amount
7. Empty state displayed when no transactions exist with friendly message and call-to-action to add first transaction
8. Loading state displayed while fetching data from API
9. Error state displayed if API call fails with retry option
10. Pagination controls displayed if more than 20 transactions (page size configurable)
11. Mobile-responsive: table converts to card layout on screens < 640px
12. TransactionService (Angular) created with methods: getTransactions(), getTransaction(id), deleteTransaction(id)
13. Component tests validate rendering and user interactions

## Story 2.4: Add Transaction Form

As a user,
I want to add a new transaction with all relevant details,
so that I can record my income and expenses accurately and build my financial dataset.

**Acceptance Criteria:**

1. AddTransactionComponent created with reactive form or template-driven form
2. Form fields: Amount (number input, required, positive validation), Date (date picker, required, not future), Description (text input, required, max 255 chars), Category (dropdown select, required), Transaction Type (toggle or radio buttons for Income/Expense, required)
3. Category dropdown populated from GET /api/categories, filtered by transaction type (show income categories when Income selected, expense categories when Expense selected)
4. Date picker defaults to today's date
5. Amount input supports decimal values (e.g., 12.99) with appropriate step (0.01)
6. Form validation displays inline error messages below each field
7. Submit button disabled until form is valid
8. Submit calls POST /api/transactions via TransactionService
9. Success displays confirmation message (toast or snackbar) and redirects to transaction list or clears form for next entry
10. Failure displays error message from API
11. Loading state disables form during submission
12. Form accessible via floating action button (FAB) or prominent "Add Transaction" button on transaction list page
13. Modal or slide-in panel UI pattern for form (alternative: dedicated /transactions/new route)
14. Keyboard accessible with proper tab order and Enter to submit
15. Form includes Cancel button to close without saving

## Story 2.5: Edit Transaction Form

As a user,
I want to edit existing transactions to correct errors or update information,
so that my financial data remains accurate and up-to-date.

**Acceptance Criteria:**

1. Edit functionality triggered by clicking Edit icon/button in transaction list
2. EditTransactionComponent created (or AddTransactionComponent reused in edit mode)
3. Form pre-populated with existing transaction data fetched via GET /api/transactions/{id}
4. All fields editable with same validation rules as Add Transaction form
5. Submit calls PUT /api/transactions/{id} via TransactionService
6. Success displays confirmation message and updates transaction in list without full page reload (optimistic update or refetch)
7. Failure displays error message and reverts to previous state
8. Cancel button discards changes and closes form
9. Loading state displayed while fetching transaction data for edit
10. 404 handling if transaction no longer exists (deleted by another session)
11. Form accessible and keyboard navigable
12. Component tests validate edit flow and data population

## Story 2.6: Delete Transaction Functionality

As a user,
I want to delete transactions I no longer want to track,
so that I can remove errors or transactions that are no longer relevant to my budgeting.

**Acceptance Criteria:**

1. Delete functionality triggered by clicking Delete icon/button in transaction list
2. Confirmation dialog displayed before deletion: "Are you sure you want to delete this transaction? This action cannot be undone."
3. Confirmation dialog includes transaction description and amount for verification
4. Confirm button calls DELETE /api/transactions/{id} via TransactionService
5. Success displays confirmation message (toast/snackbar) and removes transaction from list
6. Failure displays error message (e.g., "Failed to delete transaction. Please try again.")
7. Transaction removed from UI optimistically or after API confirmation
8. Loading state displayed during deletion (disable delete button, show spinner)
9. Cancel button on confirmation dialog aborts deletion
10. Accessibility: confirmation dialog keyboard accessible (Tab to navigate, Enter to confirm, Esc to cancel)
11. Component tests validate deletion flow and confirmation dialog

## Story 2.7: Transaction Filtering and Search

As a user,
I want to filter transactions by date range, category, and transaction type,
so that I can quickly find specific transactions and analyze spending in particular areas.

**Acceptance Criteria:**

1. Filter controls added above transaction list: Date Range (from/to date pickers or preset options like "This Month", "Last Month"), Category (multi-select dropdown or filter chips), Transaction Type (All/Income/Expense toggle)
2. Applying filters updates transaction list without page reload
3. Filters applied as query parameters to GET /api/transactions endpoint
4. Backend supports filtering: dateFrom, dateTo, categoryId (can be multiple), transactionType parameters
5. Filter state persists during session (stored in component state or query parameters in URL)
6. "Clear Filters" button resets all filters to defaults
7. Filtered results count displayed (e.g., "Showing 15 of 120 transactions")
8. Empty state displayed when filters return no results: "No transactions match your filters. Try adjusting your criteria."
9. Date range validation: "to" date cannot be before "from" date
10. Filters work in combination (AND logic: date range AND category AND type)
11. Loading state displayed while fetching filtered results
12. Mobile-responsive: filters collapse into expandable panel or drawer on small screens
