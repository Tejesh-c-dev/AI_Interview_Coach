package com.aicoach.auth.dto;

/** Token returned after successful authentication. */
public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn
) {
}
