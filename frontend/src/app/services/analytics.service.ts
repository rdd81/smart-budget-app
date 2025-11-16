import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CategoryBreakdownResponse, SummaryResponse, TrendDataPoint } from '../models/analytics.model';
import { environment } from '../../environments/environment';

export interface SummaryQueryParams {
  startDate?: string;
  endDate?: string;
}

export interface CategoryBreakdownQueryParams extends SummaryQueryParams {
  transactionType?: 'INCOME' | 'EXPENSE';
}

export interface TrendsQueryParams extends SummaryQueryParams {
  groupBy?: 'DAY' | 'WEEK' | 'MONTH';
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly summaryUrl = `${environment.apiUrl}/analytics/summary`;
  private readonly breakdownUrl = `${environment.apiUrl}/analytics/category-breakdown`;
  private readonly trendsUrl = `${environment.apiUrl}/analytics/trends`;

  getSummary(params?: SummaryQueryParams): Observable<SummaryResponse> {
    let httpParams = new HttpParams();
    if (params?.startDate) {
      httpParams = httpParams.set('startDate', params.startDate);
    }
    if (params?.endDate) {
      httpParams = httpParams.set('endDate', params.endDate);
    }

    return this.http.get<SummaryResponse>(this.summaryUrl, { params: httpParams });
  }

  getCategoryBreakdown(params?: CategoryBreakdownQueryParams): Observable<CategoryBreakdownResponse[]> {
    let httpParams = new HttpParams();
    if (params?.startDate) {
      httpParams = httpParams.set('startDate', params.startDate);
    }
    if (params?.endDate) {
      httpParams = httpParams.set('endDate', params.endDate);
    }
    if (params?.transactionType) {
      httpParams = httpParams.set('transactionType', params.transactionType);
    }
    return this.http.get<CategoryBreakdownResponse[]>(this.breakdownUrl, { params: httpParams });
  }

  getTrends(params?: TrendsQueryParams): Observable<TrendDataPoint[]> {
    let httpParams = new HttpParams();
    if (params?.startDate) {
      httpParams = httpParams.set('startDate', params.startDate);
    }
    if (params?.endDate) {
      httpParams = httpParams.set('endDate', params.endDate);
    }
    if (params?.groupBy) {
      httpParams = httpParams.set('groupBy', params.groupBy);
    }
    return this.http.get<TrendDataPoint[]>(this.trendsUrl, { params: httpParams });
  }
}
