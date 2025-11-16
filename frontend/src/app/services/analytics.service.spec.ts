import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AnalyticsService } from './analytics.service';
import { environment } from '../../environments/environment';
import { CategoryBreakdownResponse, SummaryResponse, TrendDataPoint } from '../models/analytics.model';

describe('AnalyticsService', () => {
  let service: AnalyticsService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/analytics/summary`;
  const breakdownUrl = `${environment.apiUrl}/analytics/category-breakdown`;
  const trendsUrl = `${environment.apiUrl}/analytics/trends`;
  const mockSummary: SummaryResponse = {
    totalIncome: 1000,
    totalExpenses: 400,
    balance: 600,
    transactionCount: 5,
    startDate: '2025-01-01',
    endDate: '2025-01-31'
  };

  const mockBreakdown: CategoryBreakdownResponse[] = [
    {
      categoryId: 'cat-1',
      categoryName: 'Salary',
      transactionType: 'INCOME',
      totalAmount: 800,
      transactionCount: 2,
      percentage: 70
    }
  ];

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

  it('should fetch category breakdown with params', () => {
    service.getCategoryBreakdown({ startDate: '2025-01-01', transactionType: 'EXPENSE' }).subscribe((data) => {
      expect(data).toEqual(mockBreakdown);
    });

    const req = httpMock.expectOne(request =>
      request.url === breakdownUrl &&
      request.params.get('startDate') === '2025-01-01' &&
      request.params.get('transactionType') === 'EXPENSE'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockBreakdown);
  });

  it('should fetch trends with params', () => {
    service.getTrends({ startDate: '2025-01-01', groupBy: 'WEEK' }).subscribe((data) => {
      expect(data).toEqual(mockTrends);
    });

    const req = httpMock.expectOne(request =>
      request.url === trendsUrl &&
      request.params.get('startDate') === '2025-01-01' &&
      request.params.get('groupBy') === 'WEEK'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockTrends);
  });
});
  const mockTrends: TrendDataPoint[] = [
    {
      period: '2025-01-01',
      totalIncome: 100,
      totalExpenses: 50,
      net: 50,
      transactionCount: 2
    }
  ];
