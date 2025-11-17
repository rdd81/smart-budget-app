import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategorySuggestion, CategorySuggestionRequest } from '../models/categorization.model';

@Injectable({
  providedIn: 'root',
})
export class CategorizationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/categorization/suggest`;

  /**
   * Request a category suggestion from the backend rule engine.
   */
  suggestCategory(payload: CategorySuggestionRequest): Observable<CategorySuggestion> {
    return this.http.post<CategorySuggestion>(this.apiUrl, payload);
  }
}

