package com.aicoach.execution.service;

import com.aicoach.execution.client.Judge0Client;
import com.aicoach.execution.dto.Judge0SubmissionResponse;
import com.aicoach.execution.dto.SubmissionRequest;
import com.aicoach.execution.entity.SupportedLanguage;
import com.aicoach.execution.entity.Verdict;
import com.aicoach.execution.exception.InvalidSubmissionException;
import com.aicoach.execution.exception.UnsupportedLanguageException;
import com.aicoach.execution.repository.SubmissionRepository;
import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.entity.SessionStatus;
import com.aicoach.orchestrator.exception.InactiveSessionException;
import com.aicoach.orchestrator.exception.SessionNotFoundException;
import com.aicoach.orchestrator.repository.InterviewSessionRepository;
import com.aicoach.orchestrator.repository.SessionQuestionRepository;
import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionType;
import com.aicoach.questionbank.exception.QuestionNotFoundException;
import com.aicoach.questionbank.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock Judge0Client judge0Client;
    @Mock SubmissionRepository submissionRepository;
    @Mock InterviewSessionRepository sessionRepository;
    @Mock SessionQuestionRepository sessionQuestionRepository;
    @Mock QuestionRepository questionRepository;

    // ── Happy path ────────────────────────────────────────────────────────

    @Test
    void acceptedSubmissionIsPersisted() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String userEmail = "dev@example.com";

        InterviewSession session = activeSession(sessionId);
        Question question = codingQuestion(questionId);

        when(sessionRepository.findByIdAndUserEmail(sessionId, userEmail))
                .thenReturn(Optional.of(session));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(question));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(3, "Accepted", "3", 2048));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = service().submit(
                new SubmissionRequest(sessionId, questionId, "print('hello')", "python"),
                userEmail);

        assertThat(response.verdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(response.runtimeMs()).isEqualTo(3000.0); // 3s * 1000
        assertThat(response.memoryKb()).isEqualTo(2048);
        verify(submissionRepository).save(any());
    }

    @Test
    void compileErrorIsMappedCorrectly() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String userEmail = "dev@example.com";

        when(sessionRepository.findByIdAndUserEmail(sessionId, userEmail))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(6, "Compilation Error", null, null));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = service().submit(
                new SubmissionRequest(sessionId, questionId, "class Broken { ", "java"),
                userEmail);

        assertThat(response.verdict()).isEqualTo(Verdict.COMPILE_ERROR);
    }

    @Test
    void wrongAnswerIsMappedCorrectly() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String userEmail = "dev@example.com";

        when(sessionRepository.findByIdAndUserEmail(sessionId, userEmail))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(4, "Wrong Answer", "0.1", null));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = service().submit(
                new SubmissionRequest(sessionId, questionId, "#wrong", "python"),
                userEmail);

        assertThat(response.verdict()).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void timeLimitExceededIsMappedCorrectly() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(5, "Time Limit Exceeded", "5.001", null));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = service().submit(
                new SubmissionRequest(sessionId, questionId, "while True: pass", "python"),
                "u@x.com");

        assertThat(response.verdict()).isEqualTo(Verdict.TIME_LIMIT_EXCEEDED);
    }

    @Test
    void runtimeErrorCodesAreMappedCorrectly() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        for (int statusId : new int[]{7, 8, 9, 10, 11, 12}) {
            when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                    .thenReturn(Optional.of(activeSession(sessionId)));
            when(questionRepository.findById(questionId))
                    .thenReturn(Optional.of(codingQuestion(questionId)));
            when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                    .thenReturn(true);
            when(judge0Client.submit(any()))
                    .thenReturn(judge0Response(statusId, "Runtime Error", "0.05", null));
            when(submissionRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            var response = service().submit(
                    new SubmissionRequest(sessionId, questionId, "int x = 1/0;", "java"),
                    "u@x.com");

            assertThat(response.verdict())
                    .as("status id %d should map to RUNTIME_ERROR", statusId)
                    .isEqualTo(Verdict.RUNTIME_ERROR);
        }
    }

    @Test
    void processingVerdictIsPreservedWhenJudge0TimesOut() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(2, "Processing", null, null));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = service().submit(
                new SubmissionRequest(sessionId, questionId, "while(true){}", "java"),
                "u@x.com");

        assertThat(response.verdict()).isEqualTo(Verdict.PROCESSING);
    }

    // ── Security / validation guards ──────────────────────────────────────

    @Test
    void rejectsSubmissionForSessionOwnedByAnotherUser() {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        when(sessionRepository.findByIdAndUserEmail(sessionId, "attacker@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().submit(
                new SubmissionRequest(sessionId, questionId, "code", "java"),
                "attacker@example.com"))
                .isInstanceOf(SessionNotFoundException.class);

        verifyNoInteractions(judge0Client);
    }

    @Test
    void rejectsSubmissionForCompletedSession() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        InterviewSession completed = completedSession(sessionId);

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(completed));

        assertThatThrownBy(() -> service().submit(
                new SubmissionRequest(sessionId, questionId, "code", "java"),
                "u@x.com"))
                .isInstanceOf(InactiveSessionException.class);

        verifyNoInteractions(judge0Client);
    }

    @Test
    void rejectsSubmissionForQuestionNotInSession() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(false);  // question was never served in this session

        assertThatThrownBy(() -> service().submit(
                new SubmissionRequest(sessionId, questionId, "code", "java"),
                "u@x.com"))
                .isInstanceOf(InvalidSubmissionException.class);

        verifyNoInteractions(judge0Client);
    }

    @Test
    void rejectsUnsupportedLanguage() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId)));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);

        assertThatThrownBy(() -> service().submit(
                new SubmissionRequest(sessionId, questionId, "code", "ruby"),
                "u@x.com"))
                .isInstanceOf(UnsupportedLanguageException.class);

        verifyNoInteractions(judge0Client);
    }

    @Test
    void rejectsNonCodingQuestion() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        Question nonCoding = codingQuestion(questionId);
        nonCoding.setQuestionType(QuestionType.NON_CODING);

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(nonCoding));
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);

        assertThatThrownBy(() -> service().submit(
                new SubmissionRequest(sessionId, questionId, "code", "java"),
                "u@x.com"))
                .isInstanceOf(InvalidSubmissionException.class)
                .hasMessageContaining("not a coding question");

        verifyNoInteractions(judge0Client);
    }

    @Test
    void hiddenTestsAreNotTakenFromUserInput() throws Exception {
        UUID sessionId  = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserEmail(sessionId, "u@x.com"))
                .thenReturn(Optional.of(activeSession(sessionId)));
        when(questionRepository.findById(questionId))
                .thenReturn(Optional.of(codingQuestion(questionId))); // no hiddenTestCases set
        when(sessionQuestionRepository.existsBySessionIdAndQuestionId(sessionId, questionId))
                .thenReturn(true);
        when(judge0Client.submit(any()))
                .thenReturn(judge0Response(3, "Accepted", "0.1", null));
        when(submissionRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        var captor = ArgumentCaptor.forClass(com.aicoach.execution.dto.Judge0SubmissionRequest.class);
        service().submit(new SubmissionRequest(sessionId, questionId, "code", "java"), "u@x.com");

        verify(judge0Client).submit(captor.capture());
        // stdin/expected output must come from the question entity (null here), not injected by caller
        assertThat(captor.getValue().stdin()).isNull();
        assertThat(captor.getValue().expectedOutput()).isNull();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private ExecutionService service() {
        return new ExecutionService(
                judge0Client, submissionRepository, sessionRepository,
                sessionQuestionRepository, questionRepository);
    }

    private static InterviewSession activeSession(UUID id) throws Exception {
        return session(id, SessionStatus.ACTIVE);
    }

    private static InterviewSession completedSession(UUID id) throws Exception {
        return session(id, SessionStatus.COMPLETED);
    }

    private static InterviewSession session(UUID id, SessionStatus status) throws Exception {
        InterviewSession s = new InterviewSession();
        Field f = InterviewSession.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(s, id);
        s.setStatus(status);
        return s;
    }

    private static Question codingQuestion(UUID id) throws Exception {
        Question q = new Question();
        Field f = Question.class.getDeclaredField("id");
        f.setAccessible(true);
        f.set(q, id);
        q.setQuestionType(QuestionType.CODING);
        return q;
    }

    private static Judge0SubmissionResponse judge0Response(int statusId, String description,
                                                           String time, Integer memory) {
        return new Judge0SubmissionResponse(
                "token-abc",
                null, null, null, null,
                time, memory,
                new Judge0SubmissionResponse.Judge0Status(statusId, description));
    }
}
