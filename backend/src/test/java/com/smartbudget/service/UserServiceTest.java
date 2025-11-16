package com.smartbudget.service;

import com.smartbudget.dto.UpdateEmailRequest;
import com.smartbudget.dto.UpdatePasswordRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.entity.User;
import com.smartbudget.exception.DuplicateEmailException;
import com.smartbudget.exception.InvalidCredentialsException;
import com.smartbudget.exception.ResourceNotFoundException;
import com.smartbudget.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    // getProfile tests

    @Test
    void getProfile_WithValidUserId_ShouldReturnUserResponse() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserResponse response = userService.getProfile(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        verify(userRepository).findById(userId);
    }

    @Test
    void getProfile_WithNonExistentUserId_ShouldThrowResourceNotFoundException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.getProfile(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(userId);
    }

    // updateEmail tests

    @Test
    void updateEmail_WithValidNewEmail_ShouldReturnUpdatedUserResponse() {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("newemail@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPasswordHash("hashedPassword");
        updatedUser.setCreatedAt(testUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse response = userService.updateEmail(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("newemail@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateEmail_WithSameEmail_ShouldReturnCurrentProfile() {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserResponse response = userService.updateEmail(userId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateEmail_WithDuplicateEmail_ShouldThrowDuplicateEmailException() {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("existing@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.updateEmail(userId, request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email already exists");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateEmail_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("newemail@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.updateEmail(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // updatePassword tests

    @Test
    void updatePassword_WithValidCurrentPassword_ShouldUpdatePassword() {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPassword", "newPassword123");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");

        // When
        userService.updatePassword(userId, request);

        // Then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches("currentPassword", "hashedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updatePassword_WithIncorrectCurrentPassword_ShouldThrowInvalidCredentialsException() {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrongPassword", "newPassword123");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Current password is incorrect");

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches("wrongPassword", "hashedPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updatePassword_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPassword", "newPassword123");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(userId);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
