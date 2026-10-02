package com.aicoach.execution.repository;

import com.aicoach.execution.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import com.aicoach.execution.entity.Verdict;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findBySessionIdOrderByCreatedAtDesc(UUID sessionId);
    List<Submission> findBySessionIdAndQuestionIdOrderByCreatedAtDesc(UUID sessionId, UUID questionId);
    long countBySessionId(UUID sessionId);
    long countBySessionIdAndVerdict(UUID sessionId, Verdict verdict);
}
