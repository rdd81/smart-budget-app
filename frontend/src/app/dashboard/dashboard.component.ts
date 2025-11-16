import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { User } from '../models/user.model';
import { AnalyticsService } from '../services/analytics.service';
import { SummaryResponse } from '../models/analytics.model';
import { CategoryBreakdownComponent } from './category-breakdown/category-breakdown.component';
import { ExpensePieChartComponent } from './expense-pie-chart/expense-pie-chart.component';
import { SpendingTrendsChartComponent } from './spending-trends-chart/spending-trends-chart.component';
import { MonthComparisonComponent } from './month-comparison/month-comparison.component';
import { DateRangeSelectorComponent } from './date-range-selector/date-range-selector.component';
import { DateRange } from '../models/date-range.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    DateRangeSelectorComponent,
    CategoryBreakdownComponent,
    ExpensePieChartComponent,
    SpendingTrendsChartComponent,
    MonthComparisonComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly analyticsService = inject(AnalyticsService);

  currentUser: User | null = null;
  summary: SummaryResponse | null = null;
  loading = false;
  error: string | null = null;
  selectedRange: DateRange | null = null;

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
  }

  onLogout(): void {
    this.authService.logout();
  }

  retryFetch(): void {
    if (this.selectedRange) {
      this.fetchSummary(this.selectedRange.start, this.selectedRange.end);
    }
  }

  onRangeChange(range: DateRange): void {
    this.selectedRange = range;
    this.fetchSummary(range.start, range.end);
  }

  private fetchSummary(start: string, end: string): void {
    this.loading = true;
    this.error = null;
    this.summary = null;

    this.analyticsService.getSummary({ startDate: start, endDate: end }).subscribe({
      next: (response) => {
        this.summary = response;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load analytics. Please try again.';
        this.loading = false;
      }
    });
  }

  get isEmpty(): boolean {
    return !this.loading && !this.error && (!this.summary || this.summary.transactionCount === 0);
  }

  get balanceClass(): string {
    if (!this.summary) {
      return '';
    }
    if (this.summary.balance > 0) {
      return 'positive-balance';
    }
    if (this.summary.balance < 0) {
      return 'negative-balance';
    }
    return 'neutral-balance';
  }

  formatCurrency(value: number | undefined | null): string {
    if (value === undefined || value === null) {
      return '$0.00';
    }
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }
}
