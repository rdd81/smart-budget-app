import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

export interface UpdateEmailRequest {
  newEmail: string;
}

export interface UpdatePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface UpdatePasswordResponse {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/users';

  /**
   * Get the current user's profile.
   */
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/profile`);
  }

  /**
   * Update the user's email address.
   */
  updateEmail(newEmail: string): Observable<User> {
    const request: UpdateEmailRequest = { newEmail };
    return this.http.put<User>(`${this.API_URL}/profile/email`, request);
  }

  /**
   * Update the user's password.
   */
  updatePassword(currentPassword: string, newPassword: string): Observable<UpdatePasswordResponse> {
    const request: UpdatePasswordRequest = { currentPassword, newPassword };
    return this.http.put<UpdatePasswordResponse>(`${this.API_URL}/profile/password`, request);
  }
}
