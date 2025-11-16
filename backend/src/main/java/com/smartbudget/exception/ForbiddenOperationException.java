package com.smartbudget.exception;

/**
 * Exception thrown when a user attempts to access or modify a resource they do not own.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
