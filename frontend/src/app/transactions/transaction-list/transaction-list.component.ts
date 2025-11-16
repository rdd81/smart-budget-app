import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionService } from '../../services/transaction.service';
import { Transaction, TransactionType } from '../../models/transaction.model';
import { Page } from '../../models/pagination.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.css',
})
export class TransactionListComponent implements OnInit {
  private readonly transactionService = inject(TransactionService);

  transactions: Transaction[] = [];
  loading = false;
  error: string | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  // Sorting
  sortBy: 'transactionDate' | 'amount' | 'createdAt' = 'transactionDate';
  sortDirection: 'asc' | 'desc' = 'desc';

  // Expose TransactionType enum to template
  readonly TransactionType = TransactionType;

  // Expose Math to template
  readonly Math = Math;

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(): void {
    this.loading = true;
    this.error = null;

    this.transactionService.getTransactions({
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection
    }).subscribe({
      next: (page: Page<Transaction>) => {
        this.transactions = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.currentPage = page.page;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load transactions. Please try again.';
        this.loading = false;
        console.error('Error loading transactions:', err);
      }
    });
  }

  onSort(column: 'transactionDate' | 'amount'): void {
    if (this.sortBy === column) {
      // Toggle direction
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      // New column, default to desc
      this.sortBy = column;
      this.sortDirection = 'desc';
    }
    this.currentPage = 0; // Reset to first page when sorting changes
    this.loadTransactions();
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTransactions();
    }
  }

  onRetry(): void {
    this.loadTransactions();
  }

  onDelete(transaction: Transaction): void {
    if (confirm(`Are you sure you want to delete this transaction?\n\n${transaction.description}\nAmount: $${transaction.amount.toFixed(2)}`)) {
      this.loading = true;
      this.transactionService.deleteTransaction(transaction.id).subscribe({
        next: () => {
          this.loading = false;
          // Reload transactions after successful delete
          this.loadTransactions();
        },
        error: (err) => {
          this.loading = false;
          this.error = 'Failed to delete transaction. Please try again.';
          console.error('Error deleting transaction:', err);
        }
      });
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getAmountClass(type: TransactionType): string {
    return type === TransactionType.INCOME ? 'text-green-600' : 'text-red-600';
  }

  get isEmpty(): boolean {
    return !this.loading && this.transactions.length === 0 && !this.error;
  }

  get hasPreviousPage(): boolean {
    return this.currentPage > 0;
  }

  get hasNextPage(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  getSortIcon(column: string): string {
    if (this.sortBy !== column) {
      return '';
    }
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }
}
