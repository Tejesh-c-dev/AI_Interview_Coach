package com.aicoach.orchestrator.dto;

import com.aicoach.questionbank.dto.QuestionResponse;

import java.util.UUID;

public record NextQuestionResponse(UUID sessionId, int questionNumber, QuestionResponse question) {}
