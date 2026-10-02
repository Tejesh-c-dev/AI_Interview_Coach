package com.aicoach.auth.exception;

import java.time.Instant;

/**
 * Consistent error payload returned by the {@link GlobalExceptionHandler} for
 * failed API requests.
 *
 * <p>Field names mirror Spring Boot's default error response so clients can
 * rely on one shape:</p>
 *
 * <pre>{@code
 * {
 * "timestamp": "2026-08-06T00:30:00Z",
 * "status": 409,
 * "error": "Conflict",
 * "message": "Email is already registered",
 * "path": "/api/auth/register"
 * }
 * }</pre>
 **/
public record ApiError(
                Instant timestamp,
                int status,
                String error,
                String message,
                String path) {
}
