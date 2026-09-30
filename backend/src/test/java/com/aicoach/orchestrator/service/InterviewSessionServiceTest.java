package com.aicoach.orchestrator.service;

import com.aicoach.auth.entity.User;
import com.aicoach.auth.repository.UserRepository;
import com.aicoach.orchestrator.dto.StartSessionRequest;
import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.entity.SessionStatus;
import com.aicoach.orchestrator.exception.InactiveSessionException;
import com.aicoach.orchestrator.exception.SessionNotFoundException;
import com.aicoach.orchestrator.repository.InterviewSessionRepository;
import com.aicoach.orchestrator.repository.SessionQuestionRepository;
import com.aicoach.questionbank.dto.QuestionResponse;
import com.aicoach.questionbank.entity.QuestionDifficulty;
import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionTrack;
import com.aicoach.questionbank.entity.QuestionType;
import com.aicoach.questionbank.service.QuestionBankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewSessionServiceTest {
    @Mock InterviewSessionRepository sessionRepository;
    @Mock SessionQuestionRepository sessionQuestionRepository;
    @Mock UserRepository userRepository;
    @Mock QuestionBankService questionBankService;

    @Test
    void startsSessionForAuthenticatedUserWithoutAcceptingAUserId() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(InterviewSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service().start(new StartSessionRequest("dsa", "easy", null), "user@example.com");

        assertThat(response.track()).isEqualTo("DSA");
        assertThat(response.difficulty()).isEqualTo("EASY");
        assertThat(response.status()).isEqualTo(SessionStatus.ACTIVE);
        verify(userRepository).findByEmail("user@example.com");
    }

    @Test
    void rejectsASessionOwnedByAnotherUser() {
        UUID id = UUID.randomUUID();
        when(sessionRepository.findByIdAndUserEmail(id, "other@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().nextQuestion(id, "other@example.com"))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    void rejectsQuestionsAfterCompletion() throws Exception {
        UUID id = UUID.randomUUID();
        InterviewSession session = session(SessionStatus.COMPLETED, id);
        when(sessionRepository.findByIdAndUserEmail(id, "user@example.com")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service().nextQuestion(id, "user@example.com"))
                .isInstanceOf(InactiveSessionException.class);
        verifyNoInteractions(questionBankService);
    }

    @Test
    void servesAndRecordsTheNextQuestion() throws Exception {
        UUID id = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        InterviewSession session = session(SessionStatus.ACTIVE, id);
        Question questionEntity = new Question();
        questionEntity.setId(questionId);
        QuestionResponse response = new QuestionResponse(questionId, QuestionTrack.DSA,
                QuestionDifficulty.EASY, "Arrays", "Prompt", QuestionType.CODING,
                null, null, null);
        when(sessionRepository.findByIdAndUserEmail(id, "user@example.com")).thenReturn(Optional.of(session));
        when(sessionQuestionRepository.findQuestionIdsBySessionId(id)).thenReturn(List.of());
        when(questionBankService.selectQuestion(any(), any())).thenReturn(response);
        when(questionBankService.findEntityById(questionId)).thenReturn(questionEntity);

        var next = service().nextQuestion(id, "user@example.com");

        assertThat(next.questionNumber()).isEqualTo(1);
        assertThat(next.question()).isEqualTo(response);
        verify(sessionQuestionRepository).save(any());
    }

    @Test
    void finishesAnActiveSession() throws Exception {
        UUID id = UUID.randomUUID();
        InterviewSession session = session(SessionStatus.ACTIVE, id);
        when(sessionRepository.findByIdAndUserEmail(id, "user@example.com")).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        var result = service().finish(id, "user@example.com");

        assertThat(result.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(result.endedAt()).isNotNull();
        verify(sessionRepository).save(session);
    }

    private InterviewSessionService service() {
        return new InterviewSessionService(sessionRepository, sessionQuestionRepository,
                userRepository, questionBankService);
    }

    private static InterviewSession session(SessionStatus status, UUID id) throws Exception {
        InterviewSession session = new InterviewSession();
        Field idField = InterviewSession.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(session, id);
        session.setStatus(status);
        session.setTrack(QuestionTrack.DSA.name());
        session.setDifficulty(QuestionDifficulty.EASY.name());
        return session;
    }
}
