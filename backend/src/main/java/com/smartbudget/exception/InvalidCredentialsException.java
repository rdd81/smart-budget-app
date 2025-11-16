package com.smartbudget.exception;

/**
 * Exception thrown when user login credentials are invalid.
 * Uses generic message to prevent email enumeration attacks.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
