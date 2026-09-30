package com.aicoach.questionbank.dto;

import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionDifficulty;
import com.aicoach.questionbank.entity.QuestionTrack;
import com.aicoach.questionbank.entity.QuestionType;

import java.util.UUID;

public record QuestionResponse(
        UUID id,
        QuestionTrack track,
        QuestionDifficulty difficulty,
        String topic,
        String prompt,
        QuestionType questionType,
        String codingLanguage,
        String starterCode,
        String functionSignature
) {
    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(), question.getTrack(), question.getDifficulty(),
                question.getTopic(), question.getPrompt(), question.getQuestionType(),
                question.getCodingLanguage(), question.getStarterCode(), question.getFunctionSignature());
    }
}
