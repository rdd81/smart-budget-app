import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DateRange } from '../../models/date-range.model';

interface PresetOption {
  key: string;
  label: string;
}

@Component({
  selector: 'app-date-range-selector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './date-range-selector.component.html',
  styleUrl: './date-range-selector.component.css'
})
export class DateRangeSelectorComponent implements OnInit {

  @Output() rangeChange = new EventEmitter<DateRange>();

  presets: PresetOption[] = [
    { key: 'THIS_MONTH', label: 'This Month' },
    { key: 'LAST_MONTH', label: 'Last Month' },
    { key: 'LAST_3_MONTHS', label: 'Last 3 Months' },
    { key: 'LAST_6_MONTHS', label: 'Last 6 Months' },
    { key: 'THIS_YEAR', label: 'This Year' },
    { key: 'CUSTOM', label: 'Custom Range' }
  ];

  selectedPreset = 'THIS_MONTH';
  error: string | null = null;
  customRange = { start: '', end: '' };
  activeRange: DateRange | null = null;

  private readonly storageKey = 'dashboardDateRange';

  ngOnInit(): void {
    const saved = this.loadFromStorage();
    if (saved) {
      this.selectedPreset = saved.preset;
      if (saved.preset === 'CUSTOM') {
        this.customRange = { ...saved.range };
        this.applyCustomRange();
        return;
      }
      this.applyPreset(saved.preset);
    } else {
      this.applyPreset('THIS_MONTH');
    }
  }

  selectPreset(key: string): void {
    if (key === this.selectedPreset) {
      return;
    }
    this.selectedPreset = key;
    this.error = null;
    if (key === 'CUSTOM') {
      return;
    }
    this.applyPreset(key);
  }

  applyCustomRange(): void {
    const today = this.toDateOnly(new Date());
    if (!this.customRange.start || !this.customRange.end) {
      this.error = 'Please select both start and end dates.';
      return;
    }
    const start = this.toDateOnly(new Date(this.customRange.start));
    const end = this.toDateOnly(new Date(this.customRange.end));

    if (end > today) {
      this.error = 'End date cannot be in the future.';
      return;
    }
    if (start > end) {
      this.error = 'Start date cannot be after end date.';
      return;
    }

    const range = { start: this.formatDate(start), end: this.formatDate(end) };
    this.applyRange(range, 'CUSTOM');
  }

  private applyPreset(key: string): void {
    const now = this.toDateOnly(new Date());
    let range: DateRange;
    switch (key) {
      case 'THIS_MONTH': {
        const start = new Date(now.getFullYear(), now.getMonth(), 1);
        const end = this.clampToToday(new Date(now.getFullYear(), now.getMonth() + 1, 0), now);
        range = { start: this.formatDate(start), end: this.formatDate(end) };
        break;
      }
      case 'LAST_MONTH': {
        const start = new Date(now.getFullYear(), now.getMonth() - 1, 1);
        const end = new Date(now.getFullYear(), now.getMonth(), 0);
        range = { start: this.formatDate(start), end: this.formatDate(end) };
        break;
      }
      case 'LAST_3_MONTHS': {
        const start = new Date(now.getFullYear(), now.getMonth() - 2, 1);
        const end = this.clampToToday(new Date(now.getFullYear(), now.getMonth() + 1, 0), now);
        range = { start: this.formatDate(start), end: this.formatDate(end) };
        break;
      }
      case 'LAST_6_MONTHS': {
        const start = new Date(now.getFullYear(), now.getMonth() - 5, 1);
        const end = this.clampToToday(new Date(now.getFullYear(), now.getMonth() + 1, 0), now);
        range = { start: this.formatDate(start), end: this.formatDate(end) };
        break;
      }
      case 'THIS_YEAR': {
        const start = new Date(now.getFullYear(), 0, 1);
        const end = this.clampToToday(new Date(now.getFullYear(), 11, 31), now);
        range = { start: this.formatDate(start), end: this.formatDate(end) };
        break;
      }
      default:
        range = { start: this.formatDate(now), end: this.formatDate(now) };
    }
    this.applyRange(range, key);
  }

  private applyRange(range: DateRange, preset: string): void {
    this.error = null;
    this.activeRange = range;
    this.selectedPreset = preset;
    this.persistRange(range, preset);
    this.rangeChange.emit(range);
  }

  private loadFromStorage(): { range: DateRange; preset: string } | null {
    const raw = sessionStorage.getItem(this.storageKey);
    if (!raw) {
      return null;
    }
    try {
      const parsed = JSON.parse(raw);
      if (parsed.range?.start && parsed.range?.end) {
        return {
          range: parsed.range,
          preset: parsed.preset || 'CUSTOM'
        };
      }
    } catch {
      return null;
    }
    return null;
  }

  private persistRange(range: DateRange, preset: string): void {
    sessionStorage.setItem(this.storageKey, JSON.stringify({ range, preset }));
  }

  private toDateOnly(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private clampToToday(date: Date, today: Date): Date {
    return date > today ? today : date;
  }
}
