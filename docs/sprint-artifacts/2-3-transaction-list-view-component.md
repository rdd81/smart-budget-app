# Story 2.3: Transaction List View Component

Status: ready-for-dev

## Story

As a user,
I want to view a list of all my transactions in a clear, organized table,
so that I can see my complete financial history at a glance and quickly find specific transactions.

## Acceptance Criteria

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

## Tasks / Subtasks

- [ ] Task 1: Create TransactionService (AC: #12)
  - [ ] Generate Angular service: `ng generate service services/transaction`
  - [ ] Implement getTransactions() method with pagination support (page, size, sort parameters)
  - [ ] Implement getTransaction(id) method for fetching single transaction
  - [ ] Implement deleteTransaction(id) method
  - [ ] Configure HTTP client with proper headers and error handling
  - [ ] Define Transaction interface matching backend TransactionResponse DTO
  - [ ] Define Category interface matching backend CategoryResponse DTO
  - [ ] Add proper TypeScript types for all service methods

- [ ] Task 2: Create TransactionListComponent (AC: #1, #2, #11)
  - [ ] Generate component: `ng generate component transactions/transaction-list`
  - [ ] Design responsive table layout using Tailwind CSS utility classes
  - [ ] Implement card layout for mobile screens (<640px breakpoint)
  - [ ] Add table columns: Date, Description, Category, Amount, Type, Actions
  - [ ] Add Edit and Delete action icons using Heroicons or Material Icons
  - [ ] Configure component routing in app.routes.ts

- [ ] Task 3: Implement data fetching and display (AC: #5)
  - [ ] Inject TransactionService into component
  - [ ] Implement ngOnInit to fetch transactions on component load
  - [ ] Subscribe to getTransactions() observable
  - [ ] Default sort: transactionDate descending (most recent first)
  - [ ] Store fetched transactions in component state
  - [ ] Render transactions in table rows / cards

- [ ] Task 4: Implement amount and date formatting (AC: #3, #4)
  - [ ] Use Angular CurrencyPipe for amount formatting ($1,234.56)
  - [ ] Add conditional CSS classes for amount color (green for INCOME, red for EXPENSE)
  - [ ] Use Angular DatePipe for date formatting (mediumDate format)
  - [ ] Ensure formatting is locale-aware (en-US default)

- [ ] Task 5: Implement column sorting (AC: #6)
  - [ ] Add click handlers to Date and Amount column headers
  - [ ] Track current sort column and direction in component state
  - [ ] Toggle sort direction (asc/desc) on repeated clicks
  - [ ] Update API call parameters (sortBy, sortDirection) when sort changes
  - [ ] Add visual indicators for active sort column (arrow icons)

- [ ] Task 6: Implement pagination (AC: #10)
  - [ ] Add pagination controls (Previous, Next, Page numbers) at bottom of table
  - [ ] Track current page and page size in component state (default page size: 20)
  - [ ] Update API call with page parameter when navigating
  - [ ] Display total pages and current page information
  - [ ] Disable Previous on first page, disable Next on last page
  - [ ] Consider using Angular Material Paginator or custom implementation

- [ ] Task 7: Implement loading state (AC: #8)
  - [ ] Add loading boolean flag to component state
  - [ ] Set loading=true before API call, loading=false after response
  - [ ] Display loading spinner or skeleton cards while loading
  - [ ] Use Tailwind CSS animate-spin utility for spinner
  - [ ] Ensure loading state is accessible (aria-busy attribute)

- [ ] Task 8: Implement error state (AC: #9)
  - [ ] Add error string property to component state
  - [ ] Handle error in subscription error callback
  - [ ] Display error message in user-friendly format
  - [ ] Add "Retry" button that re-triggers data fetch
  - [ ] Use error alert component with red background and error icon

- [ ] Task 9: Implement empty state (AC: #7)
  - [ ] Check if transactions array is empty after successful fetch
  - [ ] Display empty state UI with friendly message: "No transactions yet"
  - [ ] Add call-to-action button: "Add Your First Transaction"
  - [ ] Link CTA button to add transaction route/modal
  - [ ] Use empty state illustration or icon for visual appeal

- [ ] Task 10: Write component unit tests (AC: #13)
  - [ ] Set up component test bed with TransactionService mock
  - [ ] Test: component loads and fetches transactions on init
  - [ ] Test: transactions render correctly in table
  - [ ] Test: amount formatting displays correct currency and colors
  - [ ] Test: date formatting displays user-friendly dates
  - [ ] Test: sort functionality toggles correctly
  - [ ] Test: pagination controls work correctly
  - [ ] Test: loading state displays spinner
  - [ ] Test: error state displays error message and retry button
  - [ ] Test: empty state displays when no transactions
  - [ ] Test: mobile responsive layout (card view)
  - [ ] Achieve >80% code coverage for component

## Dev Notes

### Architecture & Patterns

This story implements the **Transaction List View** as the first frontend component in Epic 2, establishing the foundation for transaction management UI. The component follows Angular best practices with:

- **Reactive patterns**: Using RxJS observables for async data fetching
- **Smart/Presentational component pattern**: TransactionListComponent is a smart component managing state and API calls
- **Responsive design**: Mobile-first approach with Tailwind CSS utility classes
- **Accessibility**: WCAG AA compliance with proper ARIA labels and keyboard navigation

### Backend API Dependencies

This component depends on the **Backend Transaction CRUD API** (Story 2.1), which should be implemented first:

- **GET /api/transactions** - Paginated transaction list with query parameters:
  - `page` (default: 0)
  - `size` (default: 20)
  - `sortBy` (options: transactionDate, amount, createdAt)
  - `sortDirection` (options: asc, desc)

- **Expected Response** (TransactionResponse[]):
  ```typescript
  {
    id: string;
    amount: number;
    transactionDate: string; // ISO 8601 date
    description: string;
    category: {
      id: string;
      name: string;
      type: 'INCOME' | 'EXPENSE';
    };
    transactionType: 'INCOME' | 'EXPENSE';
    createdAt: string;
    updatedAt: string;
  }
  ```

- **Pagination Metadata** (Spring Page response):
  ```typescript
  {
    content: TransactionResponse[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
  }
  ```

**Important**: If Story 2.1 is not complete, use mock data or HTTP interceptors for development.

### Frontend Technology Stack

- **Angular 17+**: Standalone components with signals (optional)
- **Tailwind CSS**: Utility-first styling for responsive layouts
- **RxJS**: Observable-based reactive programming
- **Heroicons**: Icon library for Edit/Delete actions
- **Angular Router**: Navigation to add/edit transaction routes

### Responsive Breakpoints

From [architecture.md - PRD Section](../architecture.md#responsive-breakpoints):

- **Mobile**: 320px - 639px → Card layout, single column, stacked content
- **Tablet**: 640px - 1023px → Table layout with condensed columns
- **Desktop**: 1024px+ → Full table layout with all columns visible

### Testing Strategy

From [architecture.md - Testing Strategy](../architecture.md#testing-strategy):

- **Unit Tests**: Jasmine/Karma for component logic (target 60%+ coverage)
- **Component Tests**: Angular TestBed for template interactions
- **Mock Services**: Use jasmine.createSpyObj for TransactionService
- **Accessibility**: Test keyboard navigation and screen reader support

### Security Considerations

- **JWT Authorization**: All API calls include JWT token via HTTP interceptor (from Story 1.7)
- **User Ownership**: Backend filters transactions by authenticated user_id (no additional frontend checks needed)
- **XSS Prevention**: Angular's built-in sanitization prevents XSS in transaction descriptions

### Project Structure Notes

New files will be created in the following locations:

```
frontend/src/app/
├── models/
│   ├── transaction.model.ts          # Transaction & Category interfaces
│   └── pagination.model.ts           # Page metadata interface
├── services/
│   └── transaction.service.ts        # TransactionService with HTTP calls
└── transactions/
    └── transaction-list/
        ├── transaction-list.component.ts
        ├── transaction-list.component.html
        ├── transaction-list.component.css
        └── transaction-list.component.spec.ts
```

Add route to `app.routes.ts`:
```typescript
{ path: 'transactions', component: TransactionListComponent, canActivate: [authGuard] }
```

### References

- [PRD - Epic 2: Transaction Management](../../prd.md#epic-2-transaction-management)
- [PRD - Story 2.3: Transaction List View Component](../../prd.md#story-23-transaction-list-view-component)
- [Architecture - API Specification](../../architecture.md#api-specification)
- [Architecture - Data Models - Transaction](../../architecture.md#transaction)
- [Architecture - Tech Stack](../../architecture.md#tech-stack)
- [Architecture - Testing Strategy](../../architecture.md#testing-strategy)

## Dev Agent Record

### Context Reference

<!-- Path(s) to story context XML will be added here by context workflow -->

### Agent Model Used

<!-- Will be filled during story implementation -->

### Debug Log References

<!-- Will be added during implementation -->

### Completion Notes List

<!-- Will be added upon story completion -->

### File List

<!-- Will be added upon story completion -->
