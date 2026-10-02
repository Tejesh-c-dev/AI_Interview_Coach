package com.aicoach.progress.dto;

import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.entity.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public record InterviewHistoryResponse(
        UUID id,
        String track,
        String difficulty,
        Instant startedAt,
        Instant endedAt,
        SessionStatus status,
        long submissionCount,
        long acceptedSubmissionCount) {
    public static InterviewHistoryResponse from(InterviewSession session,
                                                  long submissionCount,
                                                  long acceptedSubmissionCount) {
        return new InterviewHistoryResponse(session.getId(), session.getTrack(), session.getDifficulty(),
                session.getStartedAt(), session.getEndedAt(), session.getStatus(),
                submissionCount, acceptedSubmissionCount);
    }
}
