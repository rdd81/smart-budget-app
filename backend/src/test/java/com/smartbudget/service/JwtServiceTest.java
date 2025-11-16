package com.smartbudget.service;

import com.smartbudget.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JwtService.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-for-jwt-token-signing-must-be-256-bits-minimum");

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        UUID extractedUserId = jwtService.extractUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(testUser.getId());
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertThat(extractedEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateToken(testUser);

        // When
        boolean isValid = jwtService.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtService.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateToken(testUser);
        String tamperedToken = token + "tampered";

        // When
        boolean isValid = jwtService.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void generateToken_ShouldCreateDifferentTokensForDifferentUsers() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordHash("hashedPassword");
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setUpdatedAt(LocalDateTime.now());

        // When
        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(anotherUser);

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }
}
