import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth/auth.service';
import { User } from '../models/user.model';
import { AnalyticsService } from '../services/analytics.service';
import { SummaryResponse } from '../models/analytics.model';
import { CategoryBreakdownComponent } from './category-breakdown/category-breakdown.component';
import { ExpensePieChartComponent } from './expense-pie-chart/expense-pie-chart.component';
import { SpendingTrendsChartComponent } from './spending-trends-chart/spending-trends-chart.component';
import { MonthComparisonComponent } from './month-comparison/month-comparison.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
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
  selectedMonthKey = '';
  isCustomRange = false;
  customRange = {
    start: '',
    end: ''
  };

  monthOptions = this.buildMonthOptions();

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.selectedMonthKey = this.monthOptions[0].key;
    this.fetchSummary(this.monthOptions[0].start, this.monthOptions[0].end);
  }

  onLogout(): void {
    this.authService.logout();
  }

  private buildMonthOptions() {
    const options = [];
    const now = new Date();
    for (let i = 0; i < 12; i++) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const start = new Date(date.getFullYear(), date.getMonth(), 1);
      const end = new Date(date.getFullYear(), date.getMonth() + 1, 0);
      options.push({
        key: `month-${i}`,
        label: start.toLocaleDateString('en-US', { month: 'long', year: 'numeric' }),
        start: start.toISOString().split('T')[0],
        end: end.toISOString().split('T')[0]
      });
    }
    options.push({
      key: 'custom',
      label: 'Custom Range',
      start: '',
      end: ''
    });
    return options;
  }

  onMonthChange(key: string): void {
    this.error = null;
    this.selectedMonthKey = key;
    this.isCustomRange = key === 'custom';

    if (!this.isCustomRange) {
      const option = this.monthOptions.find(opt => opt.key === key);
      if (option) {
        this.fetchSummary(option.start, option.end);
      }
    } else {
      this.summary = null;
    }
  }

  applyCustomRange(): void {
    if (!this.customRange.start || !this.customRange.end) {
      this.error = 'Please select both start and end dates.';
      return;
    }

    if (new Date(this.customRange.start) > new Date(this.customRange.end)) {
      this.error = 'End date cannot be before start date.';
      return;
    }

    this.fetchSummary(this.customRange.start, this.customRange.end);
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
