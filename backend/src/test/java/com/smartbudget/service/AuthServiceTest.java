package com.smartbudget.service;

import com.smartbudget.dto.RegisterRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.entity.User;
import com.smartbudget.exception.DuplicateEmailException;
import com.smartbudget.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("password123");
    }

    @Test
    void register_WithValidData_ShouldReturnUserResponse() {
        // Given
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(validRequest.getEmail());
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse response = authService.register(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedUser.getId());
        assertThat(response.getEmail()).isEqualTo(validRequest.getEmail());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        verify(userRepository).existsByEmail(validRequest.getEmail());
        verify(passwordEncoder).encode(validRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithDuplicateEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(validRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByEmail(validRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldHashPassword() {
        // Given
        String plainPassword = "password123";
        String hashedPassword = "hashedPassword";

        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(validRequest.getEmail());
        savedUser.setPasswordHash(hashedPassword);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        authService.register(validRequest);

        // Then
        verify(passwordEncoder).encode(plainPassword);
        verify(userRepository).save(argThat(user ->
                user.getPasswordHash().equals(hashedPassword) &&
                        !user.getPasswordHash().equals(plainPassword)
        ));
    }

    @Test
    void register_ShouldSaveUserWithCorrectData() {
        // Given
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(validRequest.getEmail());
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        authService.register(validRequest);

        // Then
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(validRequest.getEmail()) &&
                        user.getPasswordHash().equals("hashedPassword")
        ));
    }
}
