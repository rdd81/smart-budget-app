import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../services/analytics.service';
import { CategoryBreakdownResponse } from '../../models/analytics.model';

interface PieSegment extends CategoryBreakdownResponse {
  color: string;
  dasharray: string;
  dashoffset: number;
}

@Component({
  selector: 'app-expense-pie-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './expense-pie-chart.component.html',
  styleUrl: './expense-pie-chart.component.css'
})
export class ExpensePieChartComponent implements OnChanges {

  private readonly analyticsService = inject(AnalyticsService);
  private readonly colors = ['#f87171', '#fb923c', '#facc15', '#34d399', '#60a5fa', '#c084fc', '#f472b6'];

  @Input() startDate?: string;
  @Input() endDate?: string;

  loading = false;
  error: string | null = null;
  segments: PieSegment[] = [];
  totalAmount = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['startDate'] || changes['endDate']) {
      this.fetchData();
    }
  }

  private fetchData(): void {
    this.loading = true;
    this.error = null;
    this.segments = [];
    this.totalAmount = 0;

    this.analyticsService.getCategoryBreakdown({
      startDate: this.startDate,
      endDate: this.endDate,
      transactionType: 'EXPENSE'
    }).subscribe({
      next: (data) => {
        const expenses = data.filter(item => item.transactionType === 'EXPENSE');
        this.totalAmount = expenses.reduce((sum, item) => sum + item.totalAmount, 0);
        this.segments = this.calculateSegments(expenses);
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load expense chart.';
        this.loading = false;
      }
    });
  }

  private calculateSegments(expenses: CategoryBreakdownResponse[]): PieSegment[] {
    let offset = 25; // start from top (12 o'clock)
    return expenses.map((item, index) => {
      const color = this.colors[index % this.colors.length];
      const percentage = Math.max(Math.min(item.percentage, 100), 0);
      const segment: PieSegment = {
        ...item,
        color,
        dasharray: `${percentage} ${100 - percentage}`,
        dashoffset: offset
      };
      offset -= percentage;
      return segment;
    });
  }

  get hasData(): boolean {
    return this.segments.length > 0;
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }
}
