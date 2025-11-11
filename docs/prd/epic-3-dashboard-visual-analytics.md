# Epic 3: Dashboard & Visual Analytics

**Epic Goal:**
Build a comprehensive dashboard that aggregates transaction data into meaningful summaries and visual charts, transforming raw financial information into actionable insights. Users will see their income vs. expenses, category breakdowns, spending trends, and month-over-month comparisons at a glance, helping them understand their financial patterns and make informed decisions.

## Story 3.1: Dashboard Summary Analytics Backend API

As a developer,
I want backend API endpoints that aggregate transaction data for dashboard summaries,
so that the frontend can display total income, total expenses, balance, and category breakdowns efficiently.

**Acceptance Criteria:**

1. GET /api/analytics/summary endpoint created accepting query parameters: startDate, endDate (defaults to current month if not provided)
2. Endpoint returns SummaryResponse DTO with: totalIncome (sum of all income transactions), totalExpenses (sum of all expense transactions), balance (totalIncome - totalExpenses), transactionCount, dateRange (start and end dates used)
3. GET /api/analytics/category-breakdown endpoint returns list of CategoryBreakdownResponse: categoryId, categoryName, totalAmount, transactionCount, percentage (of total expenses or income)
4. Category breakdown calculated separately for income and expense categories
5. Queries filter by authenticated user's transactions only (user_id from JWT)
6. Queries filter by date range (transaction_date between startDate and endDate)
7. Database queries optimized using aggregation functions (SUM, COUNT, GROUP BY) rather than loading all transactions into memory
8. AnalyticsService created implementing aggregation logic
9. Repository methods created using JPQL or Criteria API for complex aggregation queries
10. Endpoints handle edge cases: no transactions in date range returns zeros, empty category breakdowns
11. Unit tests validate calculation logic with mocked data
12. Integration tests validate end-to-end with real database queries
13. API documented with OpenAPI/Swagger annotations

## Story 3.2: Dashboard Summary Cards Component

As a user,
I want to see summary cards showing my total income, total expenses, and balance for the current month,
so that I immediately understand my financial position when I open the application.

**Acceptance Criteria:**

1. DashboardComponent created as main landing page after login (route: /dashboard, default route after authentication)
2. Three summary cards displayed prominently at top: Income (green accent), Expenses (red accent), Balance (blue or contextual: green if positive, red if negative)
3. Each card shows: Label (Income/Expenses/Balance), Amount (large, formatted as currency), Icon (relevant financial icon)
4. Data fetched from GET /api/analytics/summary with current month date range
5. Month selector component added (dropdown or date picker) allowing user to select different month/date range
6. Changing month triggers API call to fetch data for selected period
7. Loading state displayed while fetching summary data (skeleton cards or spinners)
8. Error state displayed if API call fails with retry button
9. Empty state displayed if no transactions exist in selected period: "No transactions found for this period. Add your first transaction to see insights."
10. Cards responsive: stack vertically on mobile (<640px), display side-by-side on tablet/desktop (≥640px)
11. Balance card changes color based on value: green if positive, red if negative, neutral if zero
12. Component tests validate data display and month selection behavior

## Story 3.3: Category Breakdown Table/List Component

As a user,
I want to see a breakdown of my spending by category,
so that I understand where my money is going and can identify areas for optimization.

**Acceptance Criteria:**

1. CategoryBreakdownComponent created and embedded in DashboardComponent
2. Data fetched from GET /api/analytics/category-breakdown for selected date range
3. Display format: Table or list showing Category Name, Amount, Transaction Count, Percentage of Total
4. Categories sorted by amount descending (highest spending first)
5. Percentage displayed with visual indicator (progress bar or colored background)
6. Separate breakdowns for income and expense categories (two tables or tabbed view)
7. Color coding: expense categories in red/orange spectrum, income categories in green spectrum
8. Clicking on a category navigates to transaction list filtered by that category
9. Empty state if no transactions: "No spending data available for this period."
10. Loading and error states handled gracefully
11. Responsive: table converts to card layout on mobile screens
12. Top 5 categories highlighted or displayed prominently with "View All" option to expand

## Story 3.4: Expense Category Pie Chart Visualization

As a user,
I want to see a pie chart visualizing my expense breakdown by category,
so that I can quickly grasp my spending distribution through an intuitive visual representation.

**Acceptance Criteria:**

1. ExpensePieChartComponent created using Chart.js or ngx-charts library
2. Pie chart displays expense categories with segment size proportional to spending amount
3. Each segment colored distinctly (use predefined color palette for consistency)
4. Chart legend displays category names with corresponding colors
5. Hover over segment displays tooltip: Category name, Amount, Percentage
6. Chart data fetched from GET /api/analytics/category-breakdown (expense categories only)
7. Chart updates when user changes selected month/date range
8. Empty state if no expense transactions: "Add expense transactions to see spending breakdown."
9. Chart responsive: adjusts size based on container, readable on mobile devices
10. Accessibility: Alternative text or data table provided for screen readers
11. Clicking on pie segment filters transaction list to show that category (optional enhancement)
12. Chart includes title: "Expense Breakdown by Category"

## Story 3.5: Spending Trends Line/Bar Chart

As a user,
I want to see a chart showing my spending trends over time,
so that I can identify patterns, seasonal variations, and track my progress toward budget goals.

**Acceptance Criteria:**

1. SpendingTrendsChartComponent created using Chart.js or ngx-charts
2. Chart type: Line chart or bar chart (user preference or configurable toggle)
3. X-axis: Time period (months for yearly view, weeks for quarterly view, days for monthly view)
4. Y-axis: Amount in currency
5. Two data series plotted: Total Income (green line/bars), Total Expenses (red line/bars)
6. Chart fetches data from new endpoint GET /api/analytics/trends with parameters: startDate, endDate, groupBy (DAY/WEEK/MONTH)
7. Backend endpoint aggregates transactions grouped by time period (daily, weekly, or monthly)
8. Default view shows last 6 months with monthly grouping
9. Hover over data point displays tooltip: Date/Period, Income amount, Expense amount, Net (income - expense)
10. Chart includes legend identifying income and expense series
11. Chart responsive and readable on all screen sizes
12. Empty state if no data: "Add transactions to see spending trends."
13. Loading state while fetching trend data
14. Chart title: "Income vs. Expenses Over Time"

## Story 3.6: Backend Trends Analytics API

As a developer,
I want a backend API endpoint that aggregates transaction data over time periods,
so that the frontend can display spending trends charts efficiently.

**Acceptance Criteria:**

1. GET /api/analytics/trends endpoint created with query parameters: startDate, endDate, groupBy (DAY/WEEK/MONTH)
2. Endpoint returns list of TrendDataPoint DTOs: period (date or date range), totalIncome, totalExpenses, net (income - expense), transactionCount
3. Aggregation groups transactions by requested time period using database date functions
4. Data sorted chronologically (oldest to newest)
5. Handles gaps in data: periods with no transactions return zero values (not omitted) for consistent chart display
6. Queries optimized for performance with proper indexing on transaction_date
7. AnalyticsService implements trend calculation logic
8. Unit tests validate grouping logic for different time periods
9. Integration tests validate database aggregation queries
10. API documented with OpenAPI/Swagger annotations

## Story 3.7: Month-over-Month Comparison Component

As a user,
I want to see how my spending this month compares to previous months,
so that I can track my progress and identify if I'm improving or regressing in my budgeting discipline.

**Acceptance Criteria:**

1. MonthComparisonComponent created and embedded in DashboardComponent
2. Displays comparison between current month and previous month: Income (current vs. previous, % change), Expenses (current vs. previous, % change), Balance (current vs. previous, % change)
3. Percentage change calculated as: ((current - previous) / previous) * 100
4. Positive change in income or balance displayed in green with up arrow icon
5. Negative change in expenses displayed in green with down arrow (spending less is good)
6. Negative change in income or balance displayed in red with down arrow
7. Handles division by zero: if previous month is zero, display "N/A" or "New data"
8. Data fetched using existing summary endpoint with two date ranges (current month, previous month)
9. Component shows comparison in compact card or table format
10. Empty state if insufficient data (only one month of transactions): "Add more transaction history to see comparisons."

## Story 3.8: Dashboard Date Range Selector and Filtering

As a user,
I want to change the date range for all dashboard analytics,
so that I can view my financial summary for different time periods (current month, last 3 months, custom range).

**Acceptance Criteria:**

1. Date range selector component added at top of dashboard with preset options: This Month, Last Month, Last 3 Months, Last 6 Months, This Year, Custom Range
2. Selecting preset option updates all dashboard components (summary cards, charts, breakdowns) with appropriate date range
3. Custom Range option opens date picker allowing user to select arbitrary start and end dates
4. Selected date range displayed prominently: "Showing data for: January 1 - January 31, 2025"
5. Date range persists during session (stored in component state or URL query parameters)
6. All dashboard components react to date range changes and fetch updated data
7. Loading states displayed during data refresh
8. Validation: End date cannot be before start date, no future dates allowed
9. Default view on dashboard load: Current month
10. Date range selector responsive: dropdown on mobile, button group on desktop
