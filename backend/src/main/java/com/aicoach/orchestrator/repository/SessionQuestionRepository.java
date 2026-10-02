package com.aicoach.orchestrator.repository;

import com.aicoach.orchestrator.entity.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, UUID> {
    @Query("select sq.question.id from SessionQuestion sq where sq.session.id = :sessionId")
    List<UUID> findQuestionIdsBySessionId(@Param("sessionId") UUID sessionId);

    boolean existsBySessionIdAndQuestionId(UUID sessionId, UUID questionId);
}
