import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
import { Category } from '../models/transaction.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/categories`;
  private cache$?: Observable<Category[]>;

  /**
   * Fetch system-wide categories cached in-memory to avoid repeated network calls.
   */
  getCategories(forceRefresh = false): Observable<Category[]> {
    if (!this.cache$ || forceRefresh) {
      this.cache$ = this.http.get<Category[]>(this.apiUrl).pipe(shareReplay(1));
    }
    return this.cache$;
  }
}
