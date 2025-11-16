package com.smartbudget.service;

import com.smartbudget.dto.RegisterRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.entity.User;
import com.smartbudget.exception.DuplicateEmailException;
import com.smartbudget.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations including user registration.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with email and password.
     *
     * @param request Registration request with email and password
     * @return UserResponse with user details (excludes password)
     * @throws DuplicateEmailException if email already exists
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }

        // Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);

        user = userRepository.save(user);

        // Map to response DTO (excludes password)
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
