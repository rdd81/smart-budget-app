package com.smartbudget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for updating user email.
 */
@Schema(description = "Request to update user email address")
public class UpdateEmailRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Schema(description = "New email address", example = "newemail@example.com")
    private String newEmail;

    // Constructors
    public UpdateEmailRequest() {
    }

    public UpdateEmailRequest(String newEmail) {
        this.newEmail = newEmail;
    }

    // Getters and Setters
    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
