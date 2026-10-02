package com.aicoach.execution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Request body for POST /api/submissions. */
public record SubmissionRequest(
        @NotNull(message = "sessionId is required")
        UUID sessionId,

        @NotNull(message = "questionId is required")
        UUID questionId,

        @NotBlank(message = "sourceCode must not be blank")
        String sourceCode,

        @NotBlank(message = "language must not be blank")
        String language
) {}
