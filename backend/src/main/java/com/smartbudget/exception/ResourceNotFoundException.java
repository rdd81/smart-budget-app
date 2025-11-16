package com.smartbudget.exception;

/**
 * Exception thrown when a requested entity cannot be located.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
