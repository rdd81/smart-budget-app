export interface SummaryResponse {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  transactionCount: number;
  startDate: string;
  endDate: string;
}

export interface CategoryBreakdownResponse {
  categoryId: string;
  categoryName: string;
  transactionType: 'INCOME' | 'EXPENSE';
  totalAmount: number;
  transactionCount: number;
  percentage: number;
}

export interface TrendDataPoint {
  period: string;
  totalIncome: number;
  totalExpenses: number;
  net: number;
  transactionCount: number;
}
