import { TransactionType } from './transaction.model';

export interface CategorySuggestion {
  categoryId: string | null;
  categoryName: string | null;
  confidence: number;
}

export interface CategorySuggestionRequest {
  description: string;
  amount?: number;
  transactionType: TransactionType;
}

