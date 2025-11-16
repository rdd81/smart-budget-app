import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SpendingTrendsChartComponent } from './spending-trends-chart.component';
import { AnalyticsService } from '../../services/analytics.service';
import { of, throwError } from 'rxjs';
import { TrendDataPoint } from '../../models/analytics.model';

const mockData: TrendDataPoint[] = [
  { period: '2025-01-01', totalIncome: 100, totalExpenses: 50, net: 50, transactionCount: 2 },
  { period: '2025-01-02', totalIncome: 0, totalExpenses: 20, net: -20, transactionCount: 1 }
];

describe('SpendingTrendsChartComponent', () => {
  let component: SpendingTrendsChartComponent;
  let fixture: ComponentFixture<SpendingTrendsChartComponent>;
  let analyticsService: jasmine.SpyObj<AnalyticsService>;

  beforeEach(async () => {
    analyticsService = jasmine.createSpyObj('AnalyticsService', ['getTrends']);
    analyticsService.getTrends.and.returnValue(of(mockData));

    await TestBed.configureTestingModule({
      imports: [SpendingTrendsChartComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SpendingTrendsChartComponent);
    component = fixture.componentInstance;
    component.startDate = '2025-01-01';
    component.endDate = '2025-01-31';
  });

  it('should fetch trends on input change', () => {
    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(analyticsService.getTrends).toHaveBeenCalledWith({
      startDate: '2025-01-01',
      endDate: '2025-01-31',
      groupBy: 'MONTH'
    });
    expect(component.points.length).toBe(2);
  });

  it('should handle API error', () => {
    analyticsService.getTrends.and.returnValue(throwError(() => new Error('fail')));

    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(component.error).toBe('Unable to load spending trends.');
  });

  it('should update preferences when changing groupBy', () => {
    component.points = mockData;
    component.setGroupBy('WEEK');
    expect(component.groupBy).toBe('WEEK');
    expect(analyticsService.getTrends).toHaveBeenCalledTimes(1 + 1); // initial + setGroupBy
  });

  it('should update chart type', () => {
    component.setChartType('bar');
    expect(component.chartType).toBe('bar');
  });
});
