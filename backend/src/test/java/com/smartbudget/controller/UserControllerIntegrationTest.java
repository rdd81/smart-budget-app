package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.dto.UpdateEmailRequest;
import com.smartbudget.dto.UpdatePasswordRequest;
import com.smartbudget.entity.User;
import com.smartbudget.repository.UserRepository;
import com.smartbudget.service.JwtService;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController (profile management endpoints).
 * Tests end-to-end flows including authentication, validation, and persistence.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String jwtToken;

    @BeforeAll
    static void ensureDockerAvailable() {
        Assumptions.assumeTrue(isDockerAvailable(), "Docker is required for Testcontainers-based tests.");
    }

    @BeforeEach
    void setUp() {
        // Clean up users table before each test
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser = userRepository.save(testUser);

        // Generate JWT token for test user
        jwtToken = jwtService.generateToken(testUser);
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    // GET /api/users/profile tests

    @Test
    void getProfile_WithValidToken_ShouldReturn200AndUserProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.email").value("testuser@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void getProfile_WithoutToken_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProfile_WithInvalidToken_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer invalidToken"))
                .andExpect(status().isUnauthorized());
    }

    // PUT /api/users/profile/email tests

    @Test
    void updateEmail_WithValidNewEmail_ShouldReturn200AndUpdatedProfile() throws Exception {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("newemail@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/profile/email")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.updatedAt").exists());

        // Verify database was updated
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void updateEmail_WithSameEmail_ShouldReturn200() throws Exception {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("testuser@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/profile/email")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    void updateEmail_WithDuplicateEmail_ShouldReturn400() throws Exception {
        // Given - create another user with different email
        User otherUser = new User();
        otherUser.setEmail("existing@example.com");
        otherUser.setPasswordHash(passwordEncoder.encode("password123"));
        userRepository.save(otherUser);

        UpdateEmailRequest request = new UpdateEmailRequest("existing@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/profile/email")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));

        // Verify email was not changed
        User unchangedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(unchangedUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    void updateEmail_WithInvalidEmailFormat_ShouldReturn400() throws Exception {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("invalid-email");

        // When & Then
        mockMvc.perform(put("/api/users/profile/email")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmail_WithoutToken_ShouldReturn401() throws Exception {
        // Given
        UpdateEmailRequest request = new UpdateEmailRequest("newemail@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/profile/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // PUT /api/users/profile/password tests

    @Test
    void updatePassword_WithValidCurrentPassword_ShouldReturn200() throws Exception {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("password123", "newPassword456");

        // When & Then
        mockMvc.perform(put("/api/users/profile/password")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        // Verify password was updated in database
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("newPassword456", updatedUser.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("password123", updatedUser.getPasswordHash())).isFalse();
    }

    @Test
    void updatePassword_WithIncorrectCurrentPassword_ShouldReturn401() throws Exception {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrongPassword", "newPassword456");

        // When & Then
        mockMvc.perform(put("/api/users/profile/password")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"));

        // Verify password was not changed
        User unchangedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches("password123", unchangedUser.getPasswordHash())).isTrue();
    }

    @Test
    void updatePassword_WithShortNewPassword_ShouldReturn400() throws Exception {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("password123", "short");

        // When & Then
        mockMvc.perform(put("/api/users/profile/password")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePassword_WithoutToken_ShouldReturn401() throws Exception {
        // Given
        UpdatePasswordRequest request = new UpdatePasswordRequest("password123", "newPassword456");

        // When & Then
        mockMvc.perform(put("/api/users/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
