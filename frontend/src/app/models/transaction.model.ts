export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export interface Category {
  id: string;
  name: string;
  type: TransactionType;
  description?: string;
}

export interface Transaction {
  id: string;
  amount: number;
  transactionDate: string; // ISO 8601 date string
  description: string;
  category: Category;
  transactionType: TransactionType;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  amount: number;
  transactionDate: string; // ISO 8601 date string
  description: string;
  categoryId: string;
  transactionType: TransactionType;
}
