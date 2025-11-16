import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExpensePieChartComponent } from './expense-pie-chart.component';
import { AnalyticsService } from '../../services/analytics.service';
import { of, throwError } from 'rxjs';
import { CategoryBreakdownResponse } from '../../models/analytics.model';

const mockExpenses: CategoryBreakdownResponse[] = [
  { categoryId: 'cat-1', categoryName: 'Rent', transactionType: 'EXPENSE', totalAmount: 800, transactionCount: 2, percentage: 50 },
  { categoryId: 'cat-2', categoryName: 'Food', transactionType: 'EXPENSE', totalAmount: 400, transactionCount: 4, percentage: 25 },
  { categoryId: 'cat-3', categoryName: 'Transport', transactionType: 'EXPENSE', totalAmount: 400, transactionCount: 3, percentage: 25 }
];

describe('ExpensePieChartComponent', () => {
  let component: ExpensePieChartComponent;
  let fixture: ComponentFixture<ExpensePieChartComponent>;
  let analyticsService: jasmine.SpyObj<AnalyticsService>;

  beforeEach(async () => {
    analyticsService = jasmine.createSpyObj('AnalyticsService', ['getCategoryBreakdown']);
    analyticsService.getCategoryBreakdown.and.returnValue(of(mockExpenses));

    await TestBed.configureTestingModule({
      imports: [ExpensePieChartComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExpensePieChartComponent);
    component = fixture.componentInstance;
    component.startDate = '2025-01-01';
    component.endDate = '2025-01-31';
  });

  it('should load expenses on date change', () => {
    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(analyticsService.getCategoryBreakdown).toHaveBeenCalled();
    expect(component.segments.length).toBe(3);
    expect(component.totalAmount).toBe(1600);
  });

  it('should handle service error', () => {
    analyticsService.getCategoryBreakdown.and.returnValue(throwError(() => new Error('fail')));

    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(component.error).toBe('Unable to load expense chart.');
  });

  it('should format currency values', () => {
    const formatted = component.formatCurrency(123.45);
    expect(formatted).toBe('$123.45');
  });
});
