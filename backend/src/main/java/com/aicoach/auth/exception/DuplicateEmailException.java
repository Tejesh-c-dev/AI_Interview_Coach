package com.aicoach.auth.exception;

/**
 * Thrown when registration is attempted with an email address that is already registered.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
