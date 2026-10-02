package com.aicoach.orchestrator.repository;

import com.aicoach.orchestrator.entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    Optional<InterviewSession> findByIdAndUserEmail(UUID id, String email);
    List<InterviewSession> findByUserIdOrderByStartedAtDesc(UUID userId);
}
