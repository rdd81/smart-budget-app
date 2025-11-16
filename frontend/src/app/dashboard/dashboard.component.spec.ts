import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../auth/auth.service';
import { AnalyticsService } from '../services/analytics.service';
import { SummaryResponse } from '../models/analytics.model';

class AuthServiceMock {
  logout = jasmine.createSpy('logout');
  getCurrentUser() {
    return { email: 'user@example.com' };
  }
}

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let analyticsService: jasmine.SpyObj<AnalyticsService>;

  const mockSummary: SummaryResponse = {
    totalIncome: 1000,
    totalExpenses: 400,
    balance: 600,
    transactionCount: 5,
    startDate: '2025-01-01',
    endDate: '2025-01-31'
  };

  beforeEach(async () => {
    analyticsService = jasmine.createSpyObj('AnalyticsService', ['getSummary']);
    analyticsService.getSummary.and.returnValue(of(mockSummary));

    await TestBed.configureTestingModule({
      imports: [DashboardComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: AnalyticsService, useValue: analyticsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should load summary on init', () => {
    fixture.detectChanges();

    expect(component.summary).toEqual(mockSummary);
    expect(analyticsService.getSummary).toHaveBeenCalled();
  });

  it('should refetch when month changes', () => {
    fixture.detectChanges();
    analyticsService.getSummary.calls.reset();
    const secondOption = component.monthOptions[1];

    component.onMonthChange(secondOption.key);

    expect(component.selectedMonthKey).toBe(secondOption.key);
    expect(analyticsService.getSummary).toHaveBeenCalledWith({
      startDate: secondOption.start,
      endDate: secondOption.end
    });
  });

  it('should handle custom range apply', () => {
    fixture.detectChanges();
    component.onMonthChange('custom');
    analyticsService.getSummary.calls.reset();

    component.customRange.start = '2025-02-01';
    component.customRange.end = '2025-02-28';
    component.applyCustomRange();

    expect(analyticsService.getSummary).toHaveBeenCalledWith({
      startDate: '2025-02-01',
      endDate: '2025-02-28'
    });
  });

  it('should show error message when service fails', () => {
    analyticsService.getSummary.and.returnValue(throwError(() => new Error('fail')));
    fixture.detectChanges();

    expect(component.error).toBe('Failed to load analytics. Please try again.');
    expect(component.summary).toBeNull();
  });
});
