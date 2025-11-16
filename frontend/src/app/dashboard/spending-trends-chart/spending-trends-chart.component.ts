import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../services/analytics.service';
import { TrendDataPoint } from '../../models/analytics.model';

type GroupByOption = 'DAY' | 'WEEK' | 'MONTH';

@Component({
  selector: 'app-spending-trends-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './spending-trends-chart.component.html',
  styleUrl: './spending-trends-chart.component.css'
})
export class SpendingTrendsChartComponent implements OnChanges {

  private readonly analyticsService = inject(AnalyticsService);
  private readonly filterStorageKey = 'trendsPreferences';

  @Input() startDate?: string;
  @Input() endDate?: string;

  loading = false;
  error: string | null = null;
  points: TrendDataPoint[] = [];
  groupBy: GroupByOption = 'MONTH';
  chartType: 'line' | 'bar' = 'line';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['startDate'] || changes['endDate']) {
      this.loadPreferences();
      this.fetchData();
    }
  }

  private loadPreferences(): void {
    const persisted = sessionStorage.getItem(this.filterStorageKey);
    if (persisted) {
      try {
        const parsed = JSON.parse(persisted);
        if (parsed.groupBy) {
          this.groupBy = parsed.groupBy;
        }
        if (parsed.chartType) {
          this.chartType = parsed.chartType;
        }
      } catch {
        // ignore
      }
    }
  }

  private persistPreferences(): void {
    sessionStorage.setItem(this.filterStorageKey, JSON.stringify({
      groupBy: this.groupBy,
      chartType: this.chartType
    }));
  }

  fetchData(): void {
    this.loading = true;
    this.error = null;
    this.points = [];

    this.analyticsService.getTrends({
      startDate: this.startDate,
      endDate: this.endDate,
      groupBy: this.groupBy
    }).subscribe({
      next: (data) => {
        this.points = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load spending trends.';
        this.loading = false;
      }
    });
  }

  get hasData(): boolean {
    return this.points.some(point => point.totalIncome !== 0 || point.totalExpenses !== 0);
  }

  setGroupBy(option: GroupByOption): void {
    this.groupBy = option;
    this.persistPreferences();
    this.fetchData();
  }

  setChartType(type: 'line' | 'bar'): void {
    this.chartType = type;
    this.persistPreferences();
  }

  trackByDate(_: number, point: TrendDataPoint): string {
    return point.period;
  }

  viewBoxHeight = 220;
  viewBoxWidth = 600;

  get incomePath(): string {
    if (this.points.length === 0) {
      return '';
    }
    const coords = this.points.map((point, index) => {
      const x = this.scaleX(index);
      const y = this.scaleY(point.totalIncome);
      return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
    });
    return coords.join(' ');
  }

  get expensePath(): string {
    if (this.points.length === 0) {
      return '';
    }
    const coords = this.points.map((point, index) => {
      const x = this.scaleX(index);
      const y = this.scaleY(point.totalExpenses);
      return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
    });
    return coords.join(' ');
  }

  get maxValue(): number {
    return Math.max(...this.points.map(point => Math.max(point.totalIncome, point.totalExpenses)), 0);
  }

  get chartHeight(): number {
    return this.viewBoxHeight - 40;
  }

  private scaleX(index: number): number {
    if (this.points.length <= 1) {
      return 40;
    }
    const usableWidth = this.viewBoxWidth - 80;
    const step = usableWidth / (this.points.length - 1);
    return 40 + index * step;
  }

  private scaleY(value: number): number {
    if (this.maxValue === 0) {
      return this.chartHeight;
    }
    const ratio = value / this.maxValue;
    return this.chartHeight - ratio * (this.chartHeight - 20);
  }
}
