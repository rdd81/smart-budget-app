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

  it('should fetch summary when range changes', () => {
    fixture.detectChanges();
    analyticsService.getSummary.calls.reset();

    component.onRangeChange({ start: '2025-03-01', end: '2025-03-31' });

    expect(component.summary).toEqual(mockSummary);
    expect(analyticsService.getSummary).toHaveBeenCalledWith({
      startDate: '2025-03-01',
      endDate: '2025-03-31'
    });
  });

  it('should show error message when service fails', () => {
    analyticsService.getSummary.and.returnValue(throwError(() => new Error('fail')));
    component.onRangeChange({ start: '2025-02-01', end: '2025-02-28' });

    expect(component.error).toBe('Failed to load analytics. Please try again.');
    expect(component.summary).toBeNull();
  });

  it('should retry fetching summary', () => {
    fixture.detectChanges();
    component.onRangeChange({ start: '2025-01-01', end: '2025-01-31' });
    analyticsService.getSummary.calls.reset();
    component.retryFetch();
    expect(analyticsService.getSummary).toHaveBeenCalled();
  });
});
