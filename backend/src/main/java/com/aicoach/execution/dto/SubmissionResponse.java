package com.aicoach.execution.dto;

import com.aicoach.execution.entity.Submission;
import com.aicoach.execution.entity.Verdict;

import java.time.Instant;
import java.util.UUID;

/** Safe, client-facing view of a submission result. */
public record SubmissionResponse(
        UUID id,
        UUID sessionId,
        UUID questionId,
        String language,
        Verdict verdict,
        Double runtimeMs,
        Integer memoryKb,
        String stdout,
        String stderr,
        String compileOutput,
        Instant createdAt
) {
    public static SubmissionResponse from(Submission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getSession().getId(),
                s.getQuestion().getId(),
                s.getLanguage(),
                s.getVerdict(),
                s.getRuntimeMs(),
                s.getMemoryKb(),
                s.getStdout(),
                s.getStderr(),
                s.getCompileOutput(),
                s.getCreatedAt());
    }
}
