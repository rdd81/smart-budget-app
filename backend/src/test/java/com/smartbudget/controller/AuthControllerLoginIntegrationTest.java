package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.dto.LoginRequest;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for user login endpoint.
 * Tests end-to-end flow including validation, authentication, and token generation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerLoginIntegrationTest {

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
    private final String testPassword = "password123";

    @BeforeAll
    static void ensureDockerAvailable() {
        Assumptions.assumeTrue(isDockerAvailable(), "Docker is required for Testcontainers-based tests.");
    }

    @BeforeEach
    void setUp() {
        // Clean up users table before each test
        userRepository.deleteAll();

        // Create test user with hashed password
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode(testPassword));
        testUser = userRepository.save(testUser);
    }

    @Test
    void login_WithValidCredentials_ShouldReturn200AndToken() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword(testPassword);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.user.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.user.createdAt").exists())
                .andExpect(jsonPath("$.user.updatedAt").exists());
    }

    @Test
    void login_WithInvalidEmail_ShouldReturn401() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword(testPassword);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturn401() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void login_WithBlankEmail_ShouldReturn400() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword(testPassword);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_WithBlankPassword_ShouldReturn400() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword("");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_WithInvalidEmailFormat_ShouldReturn400() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword(testPassword);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_ShouldReturnValidJwtToken() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword(testPassword);

        // When
        String responseContent = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then - verify token is valid
        String token = objectMapper.readTree(responseContent).get("accessToken").asText();
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // Verify token can be validated
        assertThat(jwtService.validateToken(token)).isTrue();

        // Verify token contains correct user ID
        UUID tokenUserId = jwtService.extractUserId(token);
        assertThat(tokenUserId).isEqualTo(testUser.getId());

        // Verify token contains correct email
        String tokenEmail = jwtService.extractEmail(token);
        assertThat(tokenEmail).isEqualTo(testUser.getEmail());
    }

    @Test
    void login_TokenShouldAllowAccessToProtectedEndpoint() throws Exception {
        // Given - login to get token
        LoginRequest request = new LoginRequest();
        request.setEmail(testUser.getEmail());
        request.setPassword(testPassword);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("accessToken").asText();

        // When - try to access protected endpoint with token
        // Note: We're testing that the JWT filter works, even though /api/users/profile doesn't exist yet
        // Spring Security should authenticate the request successfully (return 404, not 401/403)
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, not 401 Unauthorized
    }

    @Test
    void login_WithoutToken_ShouldNotAllowAccessToProtectedEndpoint() throws Exception {
        // When & Then - try to access protected endpoint without token
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }

    @Test
    void login_WithInvalidToken_ShouldNotAllowAccessToProtectedEndpoint() throws Exception {
        // When & Then - try to access protected endpoint with invalid token
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }
}
