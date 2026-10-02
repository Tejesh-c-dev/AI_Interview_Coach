package com.aicoach.progress.service;

import com.aicoach.auth.entity.User;
import com.aicoach.auth.repository.UserRepository;
import com.aicoach.execution.entity.Verdict;
import com.aicoach.execution.repository.SubmissionRepository;
import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.repository.InterviewSessionRepository;
import com.aicoach.progress.dto.*;
import com.aicoach.progress.entity.TopicProgress;
import com.aicoach.progress.repository.TopicProgressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProgressService {
    private final TopicProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final InterviewSessionRepository sessionRepository;
    private final SubmissionRepository submissionRepository;

    public ProgressService(TopicProgressRepository progressRepository,
                           UserRepository userRepository,
                           InterviewSessionRepository sessionRepository,
                           SubmissionRepository submissionRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public void recordSubmission(User user, String topic, Verdict verdict, Instant practicedAt) {
        TopicProgress progress = progressRepository.findByUserIdAndTopicForUpdate(user.getId(), topic)
                .orElseGet(() -> {
                    TopicProgress created = new TopicProgress();
                    created.setUser(user);
                    created.setTopic(topic);
                    return created;
                });
        progress.recordAttempt(verdict == Verdict.ACCEPTED, practicedAt);
        progressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public List<TopicProgressResponse> getProgress(UUID userId, String email) {
        requireOwner(userId, email);
        return progressRepository.findByUserIdOrderByAccuracyAscTopicAsc(userId).stream()
                .map(TopicProgressResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<InterviewHistoryResponse> getHistory(UUID userId, String email) {
        requireOwner(userId, email);
        return sessionRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(this::history).toList();
    }

    @Transactional(readOnly = true)
    public ProgressDashboardResponse getDashboard(UUID userId, String email) {
        requireOwner(userId, email);
        List<InterviewHistoryResponse> history = sessionRepository.findByUserIdOrderByStartedAtDesc(userId)
                .stream().map(this::history).toList();
        List<TopicProgressResponse> progress = progressRepository
                .findByUserIdOrderByAccuracyAscTopicAsc(userId).stream()
                .map(TopicProgressResponse::from).toList();
        List<Double> trend = history.stream()
                .filter(item -> item.submissionCount() > 0)
                .limit(10)
                .map(item -> item.acceptedSubmissionCount() * 100.0 / item.submissionCount())
                .toList();
        return new ProgressDashboardResponse(history.size(),
                history.stream().filter(item -> item.status().name().equals("COMPLETED")).count(),
                history.stream().limit(5).toList(), progress, trend);
    }

    private InterviewHistoryResponse history(InterviewSession session) {
        long total = submissionRepository.countBySessionId(session.getId());
        long accepted = submissionRepository.countBySessionIdAndVerdict(session.getId(), Verdict.ACCEPTED);
        return InterviewHistoryResponse.from(session, total, accepted);
    }

    private void requireOwner(UUID userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("Access denied");
        }
    }
}
