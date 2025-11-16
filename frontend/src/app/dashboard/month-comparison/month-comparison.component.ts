import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';
import { SummaryResponse } from '../../models/analytics.model';

interface ChangeDescriptor {
  label: string;
  value: string;
  arrow: 'up' | 'down' | 'neutral';
  colorClass: string;
}

@Component({
  selector: 'app-month-comparison',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './month-comparison.component.html',
  styleUrl: './month-comparison.component.css'
})
export class MonthComparisonComponent implements OnChanges {

  private readonly analyticsService = inject(AnalyticsService);

  @Input() startDate?: string;
  @Input() endDate?: string;

  loading = false;
  error: string | null = null;
  currentSummary: SummaryResponse | null = null;
  previousSummary: SummaryResponse | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['startDate'] || changes['endDate']) && this.startDate && this.endDate) {
      this.fetchSummaries();
    }
  }

  private fetchSummaries(): void {
    if (!this.startDate || !this.endDate) {
      return;
    }

    const previousRange = this.computePreviousRange(this.startDate);
    this.loading = true;
    this.error = null;

    forkJoin({
      current: this.analyticsService.getSummary({ startDate: this.startDate, endDate: this.endDate }),
      previous: this.analyticsService.getSummary(previousRange)
    }).subscribe({
      next: ({ current, previous }) => {
        this.currentSummary = current;
        this.previousSummary = previous;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load comparison data.';
        this.loading = false;
      }
    });
  }

  private computePreviousRange(currentStart: string) {
    const start = new Date(currentStart);
    const prevMonth = new Date(start.getFullYear(), start.getMonth() - 1, 1);
    const prevStart = prevMonth;
    const prevEnd = new Date(prevMonth.getFullYear(), prevMonth.getMonth() + 1, 0);
    return {
      startDate: prevStart.toISOString().split('T')[0],
      endDate: prevEnd.toISOString().split('T')[0]
    };
  }

  get hasData(): boolean {
    return !!(this.currentSummary && this.currentSummary.transactionCount > 0);
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) {
      return '$0.00';
    }
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  incomeChange(): ChangeDescriptor {
    return this.computeChange(this.currentSummary?.totalIncome, this.previousSummary?.totalIncome);
  }

  expenseChange(): ChangeDescriptor {
    // Invert since lower expenses are positive
    return this.computeChange(this.previousSummary?.totalExpenses, this.currentSummary?.totalExpenses, true);
  }

  balanceChange(): ChangeDescriptor {
    return this.computeChange(this.currentSummary?.balance, this.previousSummary?.balance);
  }

  private computeChange(currentValue?: number, previousValue?: number, invert = false): ChangeDescriptor {
    if (previousValue === undefined || previousValue === null) {
      previousValue = 0;
    }
    if (currentValue === undefined || currentValue === null) {
      currentValue = 0;
    }

    if (previousValue === 0) {
      return {
        label: 'Change',
        value: 'N/A',
        arrow: 'neutral',
        colorClass: 'neutral-change'
      };
    }

    const rawChange = ((currentValue - previousValue) / Math.abs(previousValue)) * 100;
    const displayChange = invert ? -rawChange : rawChange;

    let arrow: ChangeDescriptor['arrow'] = 'neutral';
    let colorClass = 'neutral-change';
    if (displayChange > 0) {
      arrow = 'up';
      colorClass = 'positive-change';
    } else if (displayChange < 0) {
      arrow = 'down';
      colorClass = 'negative-change';
    }

    return {
      label: 'Change',
      value: `${displayChange.toFixed(1)}%`,
      arrow,
      colorClass
    };
  }
}
