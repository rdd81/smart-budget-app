package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.dto.RegisterRequest;
import com.smartbudget.entity.User;
import com.smartbudget.repository.UserRepository;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for user registration endpoint.
 * Tests end-to-end flow including validation, persistence, and password hashing.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        // Clean up users table before each test
        userRepository.deleteAll();
    }

    @Test
    void register_WithValidData_ShouldReturn201AndUserResponse() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        // Verify user persisted to database
        Optional<User> savedUser = userRepository.findByEmail("newuser@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    void register_WithDuplicateEmail_ShouldReturn400() throws Exception {
        // Given - create existing user
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPasswordHash(passwordEncoder.encode("password"));
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void register_WithInvalidEmail_ShouldReturn400() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void register_WithShortPassword_ShouldReturn400() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("short");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void register_WithBlankEmail_ShouldReturn400() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void register_WithBlankPassword_ShouldReturn400() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void register_ShouldHashPassword() throws Exception {
        // Given
        String plainPassword = "mySecretPassword123";
        RegisterRequest request = new RegisterRequest();
        request.setEmail("hashtest@example.com");
        request.setPassword(plainPassword);

        // When
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Then - verify password is hashed and not stored as plain text
        Optional<User> savedUser = userRepository.findByEmail("hashtest@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getPasswordHash()).isNotEqualTo(plainPassword);
        assertThat(savedUser.get().getPasswordHash()).startsWith("$2a$10$"); // BCrypt format
        assertThat(passwordEncoder.matches(plainPassword, savedUser.get().getPasswordHash())).isTrue();
    }

    @Test
    void register_ShouldSetTimestamps() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("timestamps@example.com");
        request.setPassword("password123");

        // When
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Then
        Optional<User> savedUser = userRepository.findByEmail("timestamps@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getCreatedAt()).isNotNull();
        assertThat(savedUser.get().getUpdatedAt()).isNotNull();
        assertThat(savedUser.get().getCreatedAt()).isEqualTo(savedUser.get().getUpdatedAt());
    }
}
