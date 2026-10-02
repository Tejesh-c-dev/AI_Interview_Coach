package com.aicoach.execution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when Judge0 is unavailable or returns an unexpected error.
 * Signals a 502 so the client knows the backend could not reach the
 * execution service, rather than conflating it with a client error.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class Judge0UnavailableException extends RuntimeException {
    public Judge0UnavailableException(String message) {
        super(message);
    }

    public Judge0UnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
