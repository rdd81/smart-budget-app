# Epic 4: AI-Powered Categorization

**Epic Goal:**
Implement intelligent transaction categorization that suggests appropriate categories based on transaction description and amount patterns, reducing manual effort for users and demonstrating the "smart" value proposition. The system learns from user corrections to improve accuracy over time on a per-user basis, progressively requiring less manual intervention.

## Story 4.1: Rule-Based Category Suggestion Engine

As a developer,
I want a backend service that suggests transaction categories based on keyword matching rules,
so that the system can provide initial category suggestions before machine learning is implemented.

**Acceptance Criteria:**

1. CategorizationService created with method: suggestCategory(description, amount, transactionType) returning CategorySuggestion DTO (categoryId, categoryName, confidence score 0.0-1.0)
2. Rule engine implemented using keyword/phrase matching: Transaction description analyzed for keywords (case-insensitive) mapped to categories
3. Example rules defined: "starbucks", "coffee", "restaurant", "dinner" → Food category; "uber", "lyft", "gas", "fuel" → Transport category; "rent", "mortgage" → Rent category; "salary", "paycheck", "bonus" → Salary category
4. Rules stored in database table (categorization_rules: id, keyword, category_id, transaction_type) seeded with initial ruleset (20-30 common patterns)
5. Amount-based heuristics: Large transactions (>$1000) suggest Rent/Salary, small amounts (<$10) suggest Food/Transport
6. Confidence score calculated based on match quality: exact keyword match = 0.9, partial match = 0.6, amount heuristic = 0.4
7. Service returns null or default "Other" category if no rules match (confidence < 0.3)
8. Multi-keyword matching: description can match multiple rules, highest confidence wins
9. Unit tests validate rule matching logic with various descriptions
10. Initial ruleset covers top 10 most common categories with 3-5 keywords each

## Story 4.2: Category Suggestion API Endpoint

As a developer,
I want an API endpoint that provides category suggestions for transaction descriptions,
so that the frontend can request suggestions during transaction entry.

**Acceptance Criteria:**

1. POST /api/categorization/suggest endpoint created accepting request body: description (required), amount (optional), transactionType (INCOME/EXPENSE, required)
2. Endpoint calls CategorizationService.suggestCategory() and returns CategorySuggestionResponse: categoryId, categoryName, confidence
3. Endpoint returns 200 OK with suggestion even if confidence is low (frontend decides whether to display)
4. If no suggestion available, returns 200 with null category or default "Other" category
5. Endpoint does not require authentication for MVP (suggestions are based on generic rules, not user-specific) - ALTERNATIVE: require auth for consistency
6. Request validation: description cannot be empty, transactionType must be valid enum
7. Unit tests validate endpoint behavior with various inputs
8. Integration test validates end-to-end suggestion flow
9. API documented with OpenAPI/Swagger annotations
10. Response time under 100ms for suggestion calculation (simple rule matching)

## Story 4.3: Frontend Category Suggestion Integration

As a user,
I want the transaction form to automatically suggest a category as I type the description,
so that I can quickly accept the suggestion instead of manually selecting from the dropdown.

**Acceptance Criteria:**

1. Add/Edit Transaction form modified to call POST /api/categorization/suggest as user types description (debounced to avoid excessive API calls, 500ms delay)
2. Suggestion displayed below or next to category dropdown: "Suggested category: Food" with confidence indicator (if confidence > 0.6)
3. User can click suggestion to auto-populate category dropdown, or ignore and manually select different category
4. Suggestion updates dynamically as description changes
5. Suggestion only shown if confidence score > 0.6 (configurable threshold)
6. Loading state displayed while fetching suggestion (subtle spinner, non-intrusive)
7. No suggestion shown if API fails or returns low confidence
8. Transaction type (income/expense) affects suggestions - switching type re-triggers suggestion
9. Amount field optionally used for suggestion (if filled)
10. Keyboard shortcut or Tab key can accept suggestion (accessibility)
11. Suggestion does not override user's manual category selection (user choice takes precedence)
12. Component tests validate suggestion display and acceptance behavior

## Story 4.4: User Correction Tracking for Learning

As a developer,
I want to track when users override or correct AI category suggestions,
so that the system can learn from these corrections and improve future suggestions for that user.

**Acceptance Criteria:**

1. Database table created: categorization_feedback (id, user_id, description, suggested_category_id, actual_category_id, transaction_id, created_at)
2. When user creates transaction: if suggestion was provided but user chose different category, record feedback entry
3. When user edits transaction and changes category: record feedback entry showing original and new category
4. FeedbackService created with method: recordFeedback(userId, description, suggestedCategoryId, actualCategoryId, transactionId)
5. Feedback recorded asynchronously (does not block transaction creation/update)
6. POST /api/transactions endpoint modified to accept optional suggestedCategoryId field for tracking
7. Feedback table indexed on user_id and description for efficient querying
8. Privacy consideration: feedback data tied to specific user (user-specific learning)
9. Unit tests validate feedback recording logic
10. Integration test validates feedback persisted when transaction created with different category than suggested

## Story 4.5: Personalized Suggestion Learning

As a user,
I want the category suggestions to improve over time based on my corrections,
so that the system becomes more accurate for my specific spending patterns and requires less manual intervention.

**Acceptance Criteria:**

1. CategorizationService.suggestCategory() modified to check user-specific feedback before applying generic rules
2. User-specific pattern matching: if user has consistently corrected "Amazon" to "Shopping" (3+ times), future "Amazon" transactions suggest "Shopping" with high confidence (0.95)
3. Feedback query retrieves user's past corrections for similar descriptions (fuzzy matching or keyword overlap)
4. User-specific suggestions prioritized over generic rules (higher confidence scores)
5. Learning threshold: pattern considered learned after 3+ consistent corrections
6. User-specific rules apply only to that user's suggestions (personalized learning)
7. Generic rules still used as fallback if no user-specific patterns exist
8. Performance optimization: user feedback cached or pre-aggregated to avoid N+1 queries
9. Unit tests validate personalized suggestion logic with mocked feedback data
10. Integration test validates learning: create transaction with suggestion, correct it 3 times, verify next suggestion uses corrected category

## Story 4.6: AI Categorization Performance Metrics and Monitoring

As a product manager,
I want to track AI categorization accuracy metrics,
so that I can measure whether the system is meeting the 75%+ accuracy target and identify areas for improvement.

**Acceptance Criteria:**

1. GET /api/analytics/categorization-metrics endpoint created (admin only or internal use)
2. Metrics calculated: total suggestions made, total accepted (user kept suggested category), total rejected (user changed category), accuracy rate (accepted / total)
3. Metrics broken down by category showing which categories have highest/lowest accuracy
4. Metrics filtered by date range and user (for per-user analysis)
5. Metrics include: average confidence score for accepted vs. rejected suggestions
6. Service layer calculates metrics from categorization_feedback table
7. Dashboard view (optional admin panel) displaying metrics with charts
8. Logging implemented to track suggestion requests, responses, and user actions
9. Target metric: 75%+ accuracy rate overall by MVP completion
10. Documentation created explaining how metrics are calculated and interpreted

## Story 4.7: Bulk Categorization for Existing Transactions

As a user,
I want to apply AI categorization to my existing uncategorized or incorrectly categorized transactions,
so that I can retroactively improve my data quality without manually editing each transaction.

**Acceptance Criteria:**

1. Backend endpoint created: POST /api/transactions/bulk-categorize accepting optional filter parameters (date range, current category)
2. Endpoint retrieves all user's transactions matching filters
3. For each transaction, calls CategorizationService.suggestCategory() and updates transaction if confidence > 0.7 threshold
4. Endpoint processes in batches (e.g., 100 transactions at a time) to avoid timeouts
5. Returns summary response: totalProcessed, totalUpdated, totalSkipped (low confidence)
6. Operation runs asynchronously with job ID returned, status queryable via GET /api/jobs/{jobId}
7. Frontend button added to transaction list: "Auto-Categorize All" or "Suggest Categories" triggering bulk operation
8. Progress indicator displayed during bulk operation
9. Completion notification shows summary: "Categorized 87 of 120 transactions"
10. User can review and manually adjust any automatic categorizations
11. Unit tests validate bulk processing logic
12. Integration test validates end-to-end bulk categorization
