package com.smartbudget.service;

import com.smartbudget.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Service for JWT token generation and validation.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 hours

    public JwtService(@Value("${jwt.secret:default-secret-key-for-development-only-minimum-256-bits}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT token for a user.
     *
     * @param user User entity
     * @return JWT token string
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extract user ID from JWT token.
     *
     * @param token JWT token string
     * @return User UUID
     */
    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extract email from JWT token.
     *
     * @param token JWT token string
     * @return User email
     */
    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Validate JWT token.
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract all claims from JWT token.
     *
     * @param token JWT token string
     * @return Claims object
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
