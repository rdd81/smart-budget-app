import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService, TransactionQueryParams } from '../../services/transaction.service';
import { CategoryService } from '../../services/category.service';
import { Category, Transaction, TransactionType } from '../../models/transaction.model';
import { Page } from '../../models/pagination.model';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.css',
})
export class TransactionListComponent implements OnInit {
  private readonly transactionService = inject(TransactionService);
  private readonly categoryService = inject(CategoryService);
  private readonly filterStorageKey = 'transactionFilters';

  transactions: Transaction[] = [];
  loading = false;
  deletingId: string | null = null;
  error: string | null = null;
  deleteError: string | null = null;
  showDeleteDialog = false;
  transactionToDelete: Transaction | null = null;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  // Sorting
  sortBy: 'transactionDate' | 'amount' | 'createdAt' = 'transactionDate';
  sortDirection: 'asc' | 'desc' = 'desc';

  categories: Category[] = [];
  categoryLoading = false;
  categoryError: string | null = null;

  filters: {
    dateFrom: string | null;
    dateTo: string | null;
    transactionType: 'ALL' | TransactionType;
    categoryIds: string[];
  } = {
    dateFrom: null,
    dateTo: null,
    transactionType: 'ALL',
    categoryIds: []
  };

  filterPanelOpen = true;

  // Expose TransactionType enum to template
  readonly TransactionType = TransactionType;

  // Expose Math to template
  readonly Math = Math;

  ngOnInit(): void {
    this.loadFiltersFromStorage();
    this.loadCategories();
    this.loadTransactions();
  }

  private loadCategories(): void {
    this.categoryLoading = true;
    this.categoryService.getCategories().subscribe({
      next: categories => {
        this.categories = categories;
        this.categoryLoading = false;
      },
      error: () => {
        this.categoryLoading = false;
        this.categoryError = 'Unable to load categories. Filters may be incomplete.';
      }
    });
  }

  private loadFiltersFromStorage(): void {
    try {
      const saved = sessionStorage.getItem(this.filterStorageKey);
      if (saved) {
        const parsed = JSON.parse(saved);
        this.filters.dateFrom = parsed.dateFrom ?? null;
        this.filters.dateTo = parsed.dateTo ?? null;
        this.filters.transactionType = parsed.transactionType ?? 'ALL';
        this.filters.categoryIds = Array.isArray(parsed.categoryIds) ? parsed.categoryIds : [];
      }
    } catch {
      // ignore storage errors
    }
  }

  private persistFilters(): void {
    const payload = JSON.stringify(this.filters);
    sessionStorage.setItem(this.filterStorageKey, payload);
  }

  loadTransactions(): void {
    this.loading = true;
    this.error = null;

    const query: TransactionQueryParams = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection
    };

    if (this.filters.dateFrom) {
      query.dateFrom = this.filters.dateFrom;
    }
    if (this.filters.dateTo) {
      query.dateTo = this.filters.dateTo;
    }
    if (this.filters.transactionType !== 'ALL') {
      query.transactionType = this.filters.transactionType;
    }
    if (this.filters.categoryIds.length > 0) {
      query.categoryIds = this.filters.categoryIds;
    }

    this.transactionService.getTransactions(query).subscribe({
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

  applyFilters(): void {
    if (this.isDateRangeInvalid) {
      return;
    }
    this.currentPage = 0;
    this.persistFilters();
    this.loadTransactions();
  }

  clearFilters(): void {
    this.filters = {
      dateFrom: null,
      dateTo: null,
      transactionType: 'ALL',
      categoryIds: []
    };
    this.persistFilters();
    this.currentPage = 0;
    this.loadTransactions();
  }

  setTransactionType(type: 'ALL' | TransactionType): void {
    this.filters.transactionType = type;
  }

  toggleCategorySelection(categoryId: string, checked: boolean): void {
    if (checked) {
      if (!this.filters.categoryIds.includes(categoryId)) {
        this.filters.categoryIds = [...this.filters.categoryIds, categoryId];
      }
    } else {
      this.filters.categoryIds = this.filters.categoryIds.filter(id => id !== categoryId);
    }
  }

  isCategorySelected(categoryId: string): boolean {
    return this.filters.categoryIds.includes(categoryId);
  }

  get hasActiveFilters(): boolean {
    return !!(
      (this.filters.dateFrom && this.filters.dateFrom.trim()) ||
      (this.filters.dateTo && this.filters.dateTo.trim()) ||
      this.filters.categoryIds.length ||
      this.filters.transactionType !== 'ALL'
    );
  }

  get isDateRangeInvalid(): boolean {
    if (!this.filters.dateFrom || !this.filters.dateTo) {
      return false;
    }
    return new Date(this.filters.dateFrom) > new Date(this.filters.dateTo);
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

  openDeleteDialog(transaction: Transaction): void {
    this.transactionToDelete = transaction;
    this.showDeleteDialog = true;
    this.deleteError = null;
  }

  closeDeleteDialog(): void {
    if (this.deletingId) {
      return;
    }
    this.showDeleteDialog = false;
    this.transactionToDelete = null;
    this.deleteError = null;
  }

  confirmDelete(): void {
    if (!this.transactionToDelete || this.deletingId) {
      return;
    }

    const { id } = this.transactionToDelete;
    this.deletingId = id;
    this.deleteError = null;

    const previousTransactions = [...this.transactions];
    this.transactions = this.transactions.filter(tx => tx.id !== id);
    this.totalElements = Math.max(this.totalElements - 1, 0);

    this.transactionService.deleteTransaction(id).subscribe({
      next: () => {
        this.deletingId = null;
        this.showDeleteDialog = false;
        this.transactionToDelete = null;
        if (this.transactions.length === 0 && this.currentPage > 0) {
          this.currentPage--;
          this.loadTransactions();
        }
      },
      error: (err) => {
        this.deletingId = null;
        this.transactions = previousTransactions;
        this.totalElements = previousTransactions.length;
        this.deleteError = 'Failed to delete transaction. Please try again.';
        console.error('Error deleting transaction:', err);
      }
    });
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
