import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { environment } from '../../environments/environment';
import { SummaryResponse } from '../models/analytics.model';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/analytics/summary`;
  const mockSummary: SummaryResponse = {
    totalIncome: 1000,
    totalExpenses: 400,
    balance: 600,
    transactionCount: 5,
    startDate: '2025-01-01',
    endDate: '2025-01-31'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(AnalyticsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch summary without params', () => {
    service.getSummary().subscribe((summary) => {
      expect(summary).toEqual(mockSummary);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });

  it('should send query params when provided', () => {
    service.getSummary({ startDate: '2025-01-01', endDate: '2025-01-31' }).subscribe();

    const req = httpMock.expectOne(request =>
      request.url === apiUrl &&
      request.params.get('startDate') === '2025-01-01' &&
      request.params.get('endDate') === '2025-01-31'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });
});
