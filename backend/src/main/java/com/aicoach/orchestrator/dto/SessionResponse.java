package com.aicoach.orchestrator.dto;

import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.entity.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(UUID id, String track, String difficulty, String companyMode,
                              Instant startedAt, Instant endedAt, SessionStatus status) {
    public static SessionResponse from(InterviewSession session) {
        return new SessionResponse(session.getId(), session.getTrack(), session.getDifficulty(),
                session.getCompanyMode(), session.getStartedAt(), session.getEndedAt(), session.getStatus());
    }
}
