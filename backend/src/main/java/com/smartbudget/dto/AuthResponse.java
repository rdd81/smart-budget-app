package com.smartbudget.dto;

/**
 * DTO for authentication response containing JWT token and user details.
 */
public class AuthResponse {

    private String accessToken;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, UserResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
