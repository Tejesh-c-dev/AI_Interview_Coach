package com.aicoach.execution.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the submitted question does not belong to the given session,
 * or when the session/question relationship cannot be validated.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSubmissionException extends RuntimeException {
    public InvalidSubmissionException(String message) {
        super(message);
    }
}
