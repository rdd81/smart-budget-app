import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoryBreakdownComponent } from './category-breakdown.component';
import { AnalyticsService } from '../../services/analytics.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { CategoryBreakdownResponse } from '../../models/analytics.model';

describe('CategoryBreakdownComponent', () => {
  let component: CategoryBreakdownComponent;
  let fixture: ComponentFixture<CategoryBreakdownComponent>;
  let analyticsService: jasmine.SpyObj<AnalyticsService>;
  let router: jasmine.SpyObj<Router>;

  const mockBreakdown: CategoryBreakdownResponse[] = [
    {
      categoryId: 'cat-1',
      categoryName: 'Salary',
      transactionType: 'INCOME',
      totalAmount: 900,
      transactionCount: 2,
      percentage: 60
    },
    {
      categoryId: 'cat-2',
      categoryName: 'Rent',
      transactionType: 'EXPENSE',
      totalAmount: 500,
      transactionCount: 1,
      percentage: 50
    }
  ];

  beforeEach(async () => {
    analyticsService = jasmine.createSpyObj('AnalyticsService', ['getCategoryBreakdown']);
    analyticsService.getCategoryBreakdown.and.returnValue(of(mockBreakdown));
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [CategoryBreakdownComponent],
      providers: [
        { provide: AnalyticsService, useValue: analyticsService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryBreakdownComponent);
    component = fixture.componentInstance;
    component.startDate = '2025-01-01';
    component.endDate = '2025-01-31';
  });

  it('should load category breakdown on changes', () => {
    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any,
      endDate: {
        currentValue: '2025-01-31',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => false
      } as any
    });

    expect(analyticsService.getCategoryBreakdown).toHaveBeenCalled();
    expect(component.incomeCategories.length).toBe(1);
    expect(component.expenseCategories.length).toBe(1);
  });

  it('should handle API error', () => {
    analyticsService.getCategoryBreakdown.and.returnValue(throwError(() => new Error('fail')));

    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    expect(component.error).toBe('Unable to load category breakdown. Please try again.');
  });

  it('should store filters and navigate when viewing transactions', () => {
    component.ngOnChanges({
      startDate: {
        currentValue: '2025-01-01',
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true
      } as any
    });

    component.viewTransactions(mockBreakdown[0]);

    const stored = sessionStorage.getItem('transactionFilters');
    expect(stored).toContain('cat-1');
    expect(router.navigate).toHaveBeenCalledWith(['/transactions']);
  });
});
