package com.smartbudget.controller;

import com.smartbudget.dto.UpdateEmailRequest;
import com.smartbudget.dto.UpdatePasswordRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.exception.ErrorResponse;
import com.smartbudget.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for user profile management endpoints.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "User profile management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get authenticated user's profile.
     *
     * @param authentication the authentication object containing user ID
     * @return UserResponse with profile details
     */
    @GetMapping("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Returns the authenticated user's profile information including email and timestamps."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user email address.
     *
     * @param authentication the authentication object containing user ID
     * @param request the update email request
     * @return UserResponse with updated profile
     */
    @PutMapping("/profile/email")
    @Operation(
            summary = "Update user email",
            description = "Updates the authenticated user's email address. Email must be unique across all users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format or email already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<UserResponse> updateEmail(
            Authentication authentication,
            @Valid @RequestBody UpdateEmailRequest request) {
        UUID userId = extractUserId(authentication);
        UserResponse response = userService.updateEmail(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update user password.
     *
     * @param authentication the authentication object containing user ID
     * @param request the update password request
     * @return success message
     */
    @PutMapping("/profile/password")
    @Operation(
            summary = "Update user password",
            description = "Updates the authenticated user's password. Requires current password for verification."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password format (minimum 8 characters required)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Current password is incorrect or invalid JWT token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Map<String, String>> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UpdatePasswordRequest request) {
        UUID userId = extractUserId(authentication);
        userService.updatePassword(userId, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Extract user ID from authentication principal.
     *
     * @param authentication the authentication object
     * @return the user ID
     */
    private UUID extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("Missing authentication principal");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        if (principal instanceof String stringPrincipal) {
            return UUID.fromString(stringPrincipal);
        }

        throw new IllegalStateException("Unsupported principal type");
    }
}
