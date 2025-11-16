import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AnalyticsService } from '../../services/analytics.service';
import { CategoryBreakdownResponse } from '../../models/analytics.model';

@Component({
  selector: 'app-category-breakdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './category-breakdown.component.html',
  styleUrl: './category-breakdown.component.css'
})
export class CategoryBreakdownComponent implements OnChanges {

  private readonly analyticsService = inject(AnalyticsService);
  private readonly router = inject(Router);
  private readonly filterStorageKey = 'transactionFilters';

  @Input() startDate?: string;
  @Input() endDate?: string;

  loading = false;
  error: string | null = null;
  incomeCategories: CategoryBreakdownResponse[] = [];
  expenseCategories: CategoryBreakdownResponse[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['startDate'] || changes['endDate']) {
      this.fetchData();
    }
  }

  private fetchData(): void {
    this.loading = true;
    this.error = null;
    this.incomeCategories = [];
    this.expenseCategories = [];

    this.analyticsService.getCategoryBreakdown({
      startDate: this.startDate,
      endDate: this.endDate
    }).subscribe({
      next: (data) => {
        this.incomeCategories = data
          .filter(item => item.transactionType === 'INCOME')
          .sort((a, b) => b.totalAmount - a.totalAmount);
        this.expenseCategories = data
          .filter(item => item.transactionType === 'EXPENSE')
          .sort((a, b) => b.totalAmount - a.totalAmount);
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load category breakdown. Please try again.';
        this.loading = false;
      }
    });
  }

  get hasData(): boolean {
    return this.incomeCategories.length > 0 || this.expenseCategories.length > 0;
  }

  viewTransactions(category: CategoryBreakdownResponse): void {
    const payload = {
      dateFrom: this.startDate ?? null,
      dateTo: this.endDate ?? null,
      transactionType: category.transactionType,
      categoryIds: [category.categoryId]
    };
    sessionStorage.setItem(this.filterStorageKey, JSON.stringify(payload));
    this.router.navigate(['/transactions']);
  }

  trackByCategory(_: number, item: CategoryBreakdownResponse) {
    return item.categoryId;
  }
}
