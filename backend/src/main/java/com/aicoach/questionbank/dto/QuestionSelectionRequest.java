package com.aicoach.questionbank.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionSelectionRequest(
        @NotBlank String track,
        @NotBlank String difficulty,
        String topic,
        String selectionKey
) {}
