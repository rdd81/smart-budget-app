import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction, TransactionRequest } from '../models/transaction.model';
import { Page } from '../models/pagination.model';
import { environment } from '../../environments/environment';

export interface TransactionQueryParams {
  page?: number;
  size?: number;
  sortBy?: 'transactionDate' | 'amount' | 'createdAt';
  sortDirection?: 'asc' | 'desc';
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/transactions`;

  /**
   * Get paginated list of transactions
   */
  getTransactions(params?: TransactionQueryParams): Observable<Page<Transaction>> {
    let httpParams = new HttpParams();

    if (params) {
      if (params.page !== undefined) {
        httpParams = httpParams.set('page', params.page.toString());
      }
      if (params.size !== undefined) {
        httpParams = httpParams.set('size', params.size.toString());
      }
      if (params.sortBy) {
        httpParams = httpParams.set('sortBy', params.sortBy);
      }
      if (params.sortDirection) {
        httpParams = httpParams.set('sortDirection', params.sortDirection);
      }
    }

    return this.http.get<Page<Transaction>>(this.apiUrl, { params: httpParams });
  }

  /**
   * Get single transaction by ID
   */
  getTransaction(id: string): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }

  /**
   * Delete transaction by ID
   */
  deleteTransaction(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new transaction
   */
  createTransaction(payload: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, payload);
  }

  /**
   * Update an existing transaction
   */
  updateTransaction(id: string, payload: TransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.apiUrl}/${id}`, payload);
  }
}
