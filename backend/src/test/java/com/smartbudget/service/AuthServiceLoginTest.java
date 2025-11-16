package com.smartbudget.service;

import com.smartbudget.dto.AuthResponse;
import com.smartbudget.dto.LoginRequest;
import com.smartbudget.entity.User;
import com.smartbudget.exception.InvalidCredentialsException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService login functionality.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest validRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("password123");

        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("test@example.com");
        existingUser.setPasswordHash("$2a$10$hashedPassword");
        existingUser.setCreatedAt(LocalDateTime.now());
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Given
        String expectedToken = "jwt.token.here";

        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(validRequest.getPassword(), existingUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn(expectedToken);

        // When
        AuthResponse response = authService.login(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(expectedToken);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getId()).isEqualTo(existingUser.getId());
        assertThat(response.getUser().getEmail()).isEqualTo(existingUser.getEmail());

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(passwordEncoder).matches(validRequest.getPassword(), existingUser.getPasswordHash());
        verify(jwtService).generateToken(existingUser);
    }

    @Test
    void login_WithNonExistentEmail_ShouldThrowInvalidCredentialsException() {
        // Given
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_WithIncorrectPassword_ShouldThrowInvalidCredentialsException() {
        // Given
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(validRequest.getPassword(), existingUser.getPasswordHash())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(validRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository).findByEmail(validRequest.getEmail());
        verify(passwordEncoder).matches(validRequest.getPassword(), existingUser.getPasswordHash());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_ShouldVerifyPasswordWithCorrectArguments() {
        // Given
        String plainPassword = "password123";
        String hashedPassword = "$2a$10$hashedPassword";

        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(plainPassword, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn("token");

        // When
        authService.login(validRequest);

        // Then
        verify(passwordEncoder).matches(plainPassword, existingUser.getPasswordHash());
    }

    @Test
    void login_ShouldGenerateTokenWithCorrectUser() {
        // Given
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(validRequest.getPassword(), existingUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn("token");

        // When
        authService.login(validRequest);

        // Then
        verify(jwtService).generateToken(argThat(user ->
                user.getId().equals(existingUser.getId()) &&
                        user.getEmail().equals(existingUser.getEmail())
        ));
    }
}
