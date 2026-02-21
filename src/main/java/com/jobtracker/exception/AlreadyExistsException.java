package com.jobtracker.exception;

/**
 * Exception thrown when a resource already exists (e.g. duplicate Email or
 * Name).
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
