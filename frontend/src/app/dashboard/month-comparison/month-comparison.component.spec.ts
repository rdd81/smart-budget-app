import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MonthComparisonComponent } from './month-comparison.component';
import { AnalyticsService } from '../../services/analytics.service';
import { of, throwError } from 'rxjs';
import { SummaryResponse } from '../../models/analytics.model';

const mockCurrent: SummaryResponse = {
  totalIncome: 1000,
  totalExpenses: 600,
  balance: 400,
  transactionCount: 5,
  startDate: '2025-02-01',
  endDate: '2025-02-28'
};

const mockPrevious: SummaryResponse = {
  totalIncome: 800,
  totalExpenses: 700,
  balance: 100,
  transactionCount: 4,
  startDate: '2025-01-01',
  endDate: '2025-01-31'
};

describe('MonthComparisonComponent', () => {
  let component: MonthComparisonComponent;
  let fixture: ComponentFixture<MonthComparisonComponent>;
  let analyticsService: jasmine.SpyObj<AnalyticsService>;

  beforeEach(async () => {
    analyticsService = jasmine.createSpyObj('AnalyticsService', ['getSummary']);
    analyticsService.getSummary.and.returnValue(of(mockCurrent), of(mockPrevious));

    await TestBed.configureTestingModule({
      imports: [MonthComparisonComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MonthComparisonComponent);
    component = fixture.componentInstance;
    component.startDate = '2025-02-01';
    component.endDate = '2025-02-28';
  });

  it('should fetch current and previous summaries', () => {
    analyticsService.getSummary.and.returnValues(of(mockCurrent), of(mockPrevious));

    component.ngOnChanges({
      startDate: {
        currentValue: '2025-02-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(analyticsService.getSummary).toHaveBeenCalledTimes(2);
    expect(component.currentSummary).toEqual(mockCurrent);
    expect(component.previousSummary).toEqual(mockPrevious);
  });

  it('should handle errors', () => {
    analyticsService.getSummary.and.returnValue(throwError(() => new Error('fail')));

    component.ngOnChanges({
      startDate: {
        currentValue: '2025-02-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(component.error).toBe('Unable to load comparison data.');
  });

  it('should compute percentage changes', () => {
    analyticsService.getSummary.and.returnValues(of(mockCurrent), of(mockPrevious));
    component.ngOnChanges({
      startDate: {
        currentValue: '2025-02-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    const incomeChange = component.incomeChange();
    expect(incomeChange.value).toBe('25.0%');
    expect(incomeChange.arrow).toBe('up');

    const expenseChange = component.expenseChange();
    expect(expenseChange.arrow).toBe('down'); // lower expenses -> positive
  });
});
