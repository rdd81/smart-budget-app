package com.smartbudget.service;

import com.smartbudget.dto.AuthResponse;
import com.smartbudget.dto.LoginRequest;
import com.smartbudget.dto.RegisterRequest;
import com.smartbudget.dto.UserResponse;
import com.smartbudget.entity.User;
import com.smartbudget.exception.DuplicateEmailException;
import com.smartbudget.exception.InvalidCredentialsException;
import com.smartbudget.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations including user registration and login.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    /**
     * Authenticate user and generate JWT token.
     *
     * @param request Login request with email and password
     * @return AuthResponse with JWT token and user details
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Create user response
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        return new AuthResponse(token, userResponse);
    }
}
