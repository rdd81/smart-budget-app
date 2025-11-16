import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SummaryResponse } from '../models/analytics.model';
import { environment } from '../../environments/environment';

export interface SummaryQueryParams {
  startDate?: string;
  endDate?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly summaryUrl = `${environment.apiUrl}/analytics/summary`;

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
}
