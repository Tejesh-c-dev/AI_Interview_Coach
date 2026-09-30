package com.aicoach.auth.exception;

/** Indicates that login credentials are not valid. */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
