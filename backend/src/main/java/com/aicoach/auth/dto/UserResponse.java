package com.aicoach.auth.dto;

import com.aicoach.auth.entity.User;

import java.time.Instant;
import java.util.UUID;

/** Safe user representation that never exposes password data. */
public record UserResponse(UUID id, String name, String email, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
    }
}
