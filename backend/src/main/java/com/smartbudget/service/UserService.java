package com.smartbudget.service;

import com.smartbudget.dto.UpdateEmailRequest;
import com.smartbudget.dto.UpdatePasswordRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.entity.User;
import com.smartbudget.exception.DuplicateEmailException;
import com.smartbudget.exception.InvalidCredentialsException;
import com.smartbudget.exception.ResourceNotFoundException;
import com.smartbudget.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for user profile management operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get user profile by user ID.
     *
     * @param userId the ID of the user
     * @return UserResponse with profile details
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Update user email address.
     *
     * @param userId the ID of the user
     * @param request the update email request
     * @return UserResponse with updated profile
     * @throws ResourceNotFoundException if user not found
     * @throws DuplicateEmailException if new email already exists
     */
    @Transactional
    public UserResponse updateEmail(UUID userId, UpdateEmailRequest request) {
        // Find user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if new email is different from current email
        if (user.getEmail().equals(request.getNewEmail())) {
            // If email is the same, just return the current profile
            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }

        // Check email uniqueness
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // Update email
        user.setEmail(request.getNewEmail());
        user = userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Update user password.
     *
     * @param userId the ID of the user
     * @param request the update password request
     * @throws ResourceNotFoundException if user not found
     * @throws InvalidCredentialsException if current password is incorrect
     */
    @Transactional
    public void updatePassword(UUID userId, UpdatePasswordRequest request) {
        // Find user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Hash and update new password
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
    }
}
