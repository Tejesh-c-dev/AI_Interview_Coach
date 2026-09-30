package com.aicoach.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record StartSessionRequest(
        @NotBlank String track,
        @NotBlank String difficulty,
        Map<String, Object> configuration
) {}
