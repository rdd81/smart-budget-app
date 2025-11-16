import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../auth/user.service';
import { AuthService } from '../auth/auth.service';
import { User } from '../models/user.model';
import { ErrorResponse } from '../models/auth.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  user: User | null = null;
  loadingProfile = false;
  profileError: string | null = null;

  emailForm: FormGroup;
  loadingEmail = false;
  emailSuccess: string | null = null;
  emailError: string | null = null;

  passwordForm: FormGroup;
  loadingPassword = false;
  passwordSuccess: string | null = null;
  passwordError: string | null = null;

  constructor() {
    this.emailForm = this.fb.group({
      newEmail: ['', [Validators.required, Validators.email]]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  get newEmail() {
    return this.emailForm.get('newEmail')!;
  }

  get currentPassword() {
    return this.passwordForm.get('currentPassword')!;
  }

  get newPassword() {
    return this.passwordForm.get('newPassword')!;
  }

  get confirmPassword() {
    return this.passwordForm.get('confirmPassword')!;
  }

  /**
   * Custom validator to check if passwords match.
   */
  private passwordMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    const newPassword = group.get('newPassword');
    const confirmPassword = group.get('confirmPassword');

    if (!newPassword || !confirmPassword) {
      return null;
    }

    return newPassword.value === confirmPassword.value ? null : { passwordMismatch: true };
  }

  /**
   * Load user profile data.
   */
  loadProfile(): void {
    this.loadingProfile = true;
    this.profileError = null;

    this.userService.getProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.loadingProfile = false;
        // Pre-fill email form with current email
        this.emailForm.patchValue({ newEmail: user.email });
      },
      error: (error: HttpErrorResponse) => {
        this.loadingProfile = false;
        this.profileError = this.extractErrorMessage(error);
      }
    });
  }

  /**
   * Handle email update form submission.
   */
  onUpdateEmail(): void {
    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      return;
    }

    this.loadingEmail = true;
    this.emailSuccess = null;
    this.emailError = null;

    const { newEmail } = this.emailForm.value;

    this.userService.updateEmail(newEmail).subscribe({
      next: (updatedUser) => {
        this.loadingEmail = false;
        this.user = updatedUser;
        this.emailSuccess = 'Email updated successfully';
        // Update user in localStorage
        this.updateCurrentUserInStorage(updatedUser);
        // Clear success message after 5 seconds
        setTimeout(() => this.emailSuccess = null, 5000);
      },
      error: (error: HttpErrorResponse) => {
        this.loadingEmail = false;
        this.emailError = this.extractErrorMessage(error);
      }
    });
  }

  /**
   * Handle password update form submission.
   */
  onUpdatePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.loadingPassword = true;
    this.passwordSuccess = null;
    this.passwordError = null;

    const { currentPassword, newPassword } = this.passwordForm.value;

    this.userService.updatePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.loadingPassword = false;
        this.passwordSuccess = 'Password updated successfully';
        // Clear form
        this.passwordForm.reset();
        // Clear success message after 5 seconds
        setTimeout(() => this.passwordSuccess = null, 5000);
      },
      error: (error: HttpErrorResponse) => {
        this.loadingPassword = false;
        this.passwordError = this.extractErrorMessage(error);
      }
    });
  }

  /**
   * Update current user in localStorage to reflect email changes.
   */
  private updateCurrentUserInStorage(updatedUser: User): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      const updated = { ...currentUser, email: updatedUser.email };
      localStorage.setItem('current_user', JSON.stringify(updated));
    }
  }

  /**
   * Extract error message from HTTP error response.
   */
  private extractErrorMessage(error: HttpErrorResponse): string {
    if (error.error && typeof error.error === 'object' && 'message' in error.error) {
      const errorResponse = error.error as ErrorResponse;
      return errorResponse.message;
    }
    return 'An unexpected error occurred. Please try again.';
  }

  /**
   * Format date for display.
   */
  formatDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
