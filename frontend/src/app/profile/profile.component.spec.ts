import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ProfileComponent } from './profile.component';
import { UserService } from '../auth/user.service';
import { AuthService } from '../auth/auth.service';
import { of, throwError } from 'rxjs';
import { User } from '../models/user.model';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userService: jasmine.SpyObj<UserService>;
  let authService: jasmine.SpyObj<AuthService>;

  const mockUser: User = {
    id: '123e4567-e89b-12d3-a456-426614174000',
    email: 'test@example.com',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-15')
  };

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile', 'updateEmail', 'updatePassword']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser']);

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  beforeEach(() => {
    userService.getProfile.and.returnValue(of(mockUser));
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user profile on init', () => {
    expect(userService.getProfile).toHaveBeenCalled();
    expect(component.user).toEqual(mockUser);
    expect(component.loadingProfile).toBeFalse();
  });

  it('should display profile error when loading fails', () => {
    const errorResponse = { error: { message: 'Failed to load profile' } };
    userService.getProfile.and.returnValue(throwError(() => errorResponse));

    component.loadProfile();

    expect(component.profileError).toBeTruthy();
    expect(component.loadingProfile).toBeFalse();
  });

  it('should have valid email form when filled correctly', () => {
    component.emailForm.patchValue({ newEmail: 'new@example.com' });
    expect(component.emailForm.valid).toBeTrue();
  });

  it('should have invalid email form with invalid email', () => {
    component.emailForm.patchValue({ newEmail: 'invalid-email' });
    expect(component.emailForm.invalid).toBeTrue();
  });

  it('should update email successfully', () => {
    const updatedUser = { ...mockUser, email: 'new@example.com' };
    userService.updateEmail.and.returnValue(of(updatedUser));
    authService.getCurrentUser.and.returnValue(mockUser);

    component.emailForm.patchValue({ newEmail: 'new@example.com' });
    component.onUpdateEmail();

    expect(userService.updateEmail).toHaveBeenCalledWith('new@example.com');
    expect(component.user?.email).toBe('new@example.com');
    expect(component.emailSuccess).toBeTruthy();
  });

  it('should display error when email update fails', () => {
    const errorResponse = { error: { message: 'Email already exists' } };
    userService.updateEmail.and.returnValue(throwError(() => errorResponse));

    component.emailForm.patchValue({ newEmail: 'existing@example.com' });
    component.onUpdateEmail();

    expect(component.emailError).toBe('Email already exists');
  });

  it('should have valid password form when filled correctly', () => {
    component.passwordForm.patchValue({
      currentPassword: 'oldPassword123',
      newPassword: 'newPassword123',
      confirmPassword: 'newPassword123'
    });
    expect(component.passwordForm.valid).toBeTrue();
  });

  it('should have invalid password form when passwords do not match', () => {
    component.passwordForm.patchValue({
      currentPassword: 'oldPassword123',
      newPassword: 'newPassword123',
      confirmPassword: 'differentPassword123'
    });
    expect(component.passwordForm.invalid).toBeTrue();
    expect(component.passwordForm.errors?.['passwordMismatch']).toBeTrue();
  });

  it('should have invalid password form when new password is too short', () => {
    component.passwordForm.patchValue({
      currentPassword: 'oldPassword123',
      newPassword: 'short',
      confirmPassword: 'short'
    });
    expect(component.passwordForm.invalid).toBeTrue();
  });

  it('should update password successfully', () => {
    userService.updatePassword.and.returnValue(of({ message: 'Password updated successfully' }));

    component.passwordForm.patchValue({
      currentPassword: 'oldPassword123',
      newPassword: 'newPassword123',
      confirmPassword: 'newPassword123'
    });
    component.onUpdatePassword();

    expect(userService.updatePassword).toHaveBeenCalledWith('oldPassword123', 'newPassword123');
    expect(component.passwordSuccess).toBeTruthy();
    expect(component.passwordForm.value).toEqual({ currentPassword: null, newPassword: null, confirmPassword: null });
  });

  it('should display error when password update fails', () => {
    const errorResponse = { error: { message: 'Current password is incorrect' } };
    userService.updatePassword.and.returnValue(throwError(() => errorResponse));

    component.passwordForm.patchValue({
      currentPassword: 'wrongPassword',
      newPassword: 'newPassword123',
      confirmPassword: 'newPassword123'
    });
    component.onUpdatePassword();

    expect(component.passwordError).toBe('Current password is incorrect');
  });

  it('should format date correctly', () => {
    const date = new Date('2024-01-15');
    const formatted = component.formatDate(date);
    expect(formatted).toContain('2024');
    expect(formatted).toContain('January');
  });
});
