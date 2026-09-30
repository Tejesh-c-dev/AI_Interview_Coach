package com.aicoach.orchestrator.service;

import com.aicoach.auth.entity.User;
import com.aicoach.auth.repository.UserRepository;
import com.aicoach.orchestrator.dto.*;
import com.aicoach.orchestrator.entity.*;
import com.aicoach.orchestrator.exception.*;
import com.aicoach.orchestrator.repository.*;
import com.aicoach.questionbank.dto.QuestionResponse;
import com.aicoach.questionbank.dto.QuestionSelectionRequest;
import com.aicoach.questionbank.service.QuestionBankService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

@Service
public class InterviewSessionService {
    private final InterviewSessionRepository sessionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final UserRepository userRepository;
    private final QuestionBankService questionBankService;

    public InterviewSessionService(InterviewSessionRepository sessionRepository,
                                   SessionQuestionRepository sessionQuestionRepository,
                                   UserRepository userRepository,
                                   QuestionBankService questionBankService) {
        this.sessionRepository = sessionRepository;
        this.sessionQuestionRepository = sessionQuestionRepository;
        this.userRepository = userRepository;
        this.questionBankService = questionBankService;
    }

    @Transactional
    public SessionResponse start(StartSessionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(SessionNotFoundException::new);
        String track = questionBankService.parseTrack(request.track()).name();
        String difficulty = questionBankService.parseDifficulty(request.difficulty()).name();
        InterviewSession session = new InterviewSession();
        session.setUser(user);
        session.setTrack(track);
        session.setDifficulty(difficulty);
        session.setCompanyMode(extractCompanyMode(request.configuration()));
        session.setStartedAt(Instant.now());
        session.setStatus(SessionStatus.ACTIVE);
        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional
    public NextQuestionResponse nextQuestion(UUID id, String email) {
        InterviewSession session = ownedSession(id, email);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new InactiveSessionException();
        }
        var ids = sessionQuestionRepository.findQuestionIdsBySessionId(id);
        QuestionResponse question = questionBankService.selectQuestion(
                new QuestionSelectionRequest(session.getTrack(), session.getDifficulty(), null,
                        id + "|" + ids.size()), new HashSet<>(ids));
        var sessionQuestion = new SessionQuestion();
        sessionQuestion.setSession(session);
        sessionQuestion.setQuestion(
                questionBankService.findEntityById(question.id()));
        sessionQuestion.setQuestionOrder(ids.size() + 1);
        sessionQuestionRepository.save(sessionQuestion);
        return new NextQuestionResponse(id, ids.size() + 1, question);
    }

    @Transactional
    public SessionResponse finish(UUID id, String email) {
        InterviewSession session = ownedSession(id, email);
        if (session.getStatus() == SessionStatus.ACTIVE) {
            session.setEndedAt(Instant.now());
            session.setStatus(SessionStatus.COMPLETED);
            sessionRepository.save(session);
        }
        return SessionResponse.from(session);
    }

    private InterviewSession ownedSession(UUID id, String email) {
        return sessionRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(SessionNotFoundException::new);
    }

    private String extractCompanyMode(java.util.Map<String, Object> configuration) {
        if (configuration == null || configuration.get("companyMode") == null) {
            return null;
        }
        return configuration.get("companyMode").toString().trim();
    }
}
