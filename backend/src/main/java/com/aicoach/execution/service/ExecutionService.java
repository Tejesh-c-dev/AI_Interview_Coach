package com.aicoach.execution.service;

import com.aicoach.execution.client.Judge0Client;
import com.aicoach.execution.dto.*;
import com.aicoach.execution.entity.Submission;
import com.aicoach.execution.entity.SupportedLanguage;
import com.aicoach.execution.entity.Verdict;
import com.aicoach.execution.exception.InvalidSubmissionException;
import com.aicoach.execution.exception.UnsupportedLanguageException;
import com.aicoach.execution.repository.SubmissionRepository;
import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.orchestrator.entity.SessionStatus;
import com.aicoach.orchestrator.repository.InterviewSessionRepository;
import com.aicoach.orchestrator.repository.SessionQuestionRepository;
import com.aicoach.questionbank.entity.Question;
import com.aicoach.questionbank.entity.QuestionType;
import com.aicoach.questionbank.repository.QuestionRepository;
import com.aicoach.orchestrator.exception.InactiveSessionException;
import com.aicoach.orchestrator.exception.SessionNotFoundException;
import com.aicoach.questionbank.exception.QuestionNotFoundException;
import com.aicoach.progress.service.ProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates code submission:
 * <ol>
 * <li>Validates ownership, session state, and question/session
 * relationship</li>
 * <li>Resolves the language and hidden test cases server-side</li>
 * <li>Delegates to {@link Judge0Client} for execution</li>
 * <li>Normalises the raw Judge0 result into an application-level
 * {@link Verdict}</li>
 * <li>Persists and returns the {@link Submission}</li>
 * </ol>
 */
@Service
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    /**
     * Default execution limits applied to every submission.
     * These are applied server-side; the user cannot override them.
     */
    private static final double CPU_TIME_LIMIT_SECONDS = 5.0;
    private static final int MEMORY_LIMIT_KB = 256_000; // 256 MB
    private static final double WALL_TIME_LIMIT_SECONDS = 10.0;

    private final Judge0Client judge0Client;
    private final SubmissionRepository submissionRepository;
    private final InterviewSessionRepository sessionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ProgressService progressService;

    @Autowired
    public ExecutionService(
            Judge0Client judge0Client,
            SubmissionRepository submissionRepository,
            InterviewSessionRepository sessionRepository,
            SessionQuestionRepository sessionQuestionRepository,
            QuestionRepository questionRepository,
            ProgressService progressService) {
        this.judge0Client = judge0Client;
        this.submissionRepository = submissionRepository;
        this.sessionRepository = sessionRepository;
        this.sessionQuestionRepository = sessionQuestionRepository;
        this.questionRepository = questionRepository;
        this.progressService = progressService;
    }

    /**
     * Compatibility constructor for focused unit tests that do not exercise
     * progress tracking.
     */
    public ExecutionService(
            Judge0Client judge0Client,
            SubmissionRepository submissionRepository,
            InterviewSessionRepository sessionRepository,
            SessionQuestionRepository sessionQuestionRepository,
            QuestionRepository questionRepository) {
        this(judge0Client, submissionRepository, sessionRepository, sessionQuestionRepository,
                questionRepository, null);
    }

    /**
     * Processes a code submission request end-to-end.
     *
     * @param request   the client's submission payload
     * @param userEmail the authenticated user's email (taken from the JWT, not
     *                  trusted from input)
     * @return the persisted submission result
     */
    @Transactional
    public SubmissionResponse submit(SubmissionRequest request, String userEmail) {
        // 1. Authenticate ownership of the session
        InterviewSession session = sessionRepository
                .findByIdAndUserEmail(request.sessionId(), userEmail)
                .orElseThrow(SessionNotFoundException::new);

        // 2. Ensure the session is still active
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new InactiveSessionException();
        }

        // 3. Resolve the question and ensure it belongs to this session
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new QuestionNotFoundException("Question not found"));

        boolean questionInSession = sessionQuestionRepository
                .existsBySessionIdAndQuestionId(session.getId(), question.getId());
        if (!questionInSession) {
            throw new InvalidSubmissionException(
                    "Question does not belong to the current session");
        }

        // 4. Ensure the question is of coding type
        if (question.getQuestionType() != QuestionType.CODING) {
            throw new InvalidSubmissionException(
                    "Question is not a coding question");
        }

        // 5. Resolve and validate the requested language
        SupportedLanguage lang = SupportedLanguage.fromKey(request.language())
                .orElseThrow(() -> new UnsupportedLanguageException(request.language()));

        // 6. Build Judge0 request (hidden tests come from the Question entity, never
        // from user input)
        Judge0SubmissionRequest judge0Request = buildJudge0Request(request.sourceCode(), lang, question);

        // 7. Execute via Judge0
        log.info("Submitting for session={} question={} lang={}", session.getId(), question.getId(), lang.getKey());
        Judge0SubmissionResponse judge0Response = judge0Client.submit(judge0Request);

        // 8. Normalise verdict
        Verdict verdict = normalise(judge0Response);

        // 9. Persist and return
        Submission submission = buildSubmission(session, question, request, lang, judge0Response, verdict);
        Submission saved = submissionRepository.save(submission);
        if (progressService != null) {
            progressService.recordSubmission(session.getUser(), question.getTopic(), verdict, saved.getCreatedAt());
        }
        return SubmissionResponse.from(saved);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private Judge0SubmissionRequest buildJudge0Request(
            String sourceCode,
            SupportedLanguage lang,
            Question question) {

        // Hidden test stdin/expected output come from the question's server-side data
        // only.
        String stdin = extractStdin(question.getHiddenTestCases());
        String expectedOutput = extractExpectedOutput(question.getHiddenTestCases());

        return new Judge0SubmissionRequest(
                sourceCode,
                lang.getJudge0Id(),
                stdin,
                expectedOutput,
                CPU_TIME_LIMIT_SECONDS,
                MEMORY_LIMIT_KB,
                WALL_TIME_LIMIT_SECONDS);
    }

    /**
     * Maps Judge0 status IDs to application-level verdicts.
     *
     * <pre>
     * Judge0 status IDs (CE = Community Edition):
     *  1  In Queue
     *  2  Processing
     *  3  Accepted
     *  4  Wrong Answer
     *  5  Time Limit Exceeded
     *  6  Compilation Error
     *  7  Runtime Error (SIGSEGV)
     *  8  Runtime Error (SIGXFSZ)
     *  9  Runtime Error (SIGFPE)
     * 10  Runtime Error (SIGABRT)
     * 11  Runtime Error (NZEC)
     * 12  Runtime Error (Other)
     * 13  Internal Error
     * 14  Exec Format Error
     * </pre>
     */
    private Verdict normalise(Judge0SubmissionResponse response) {
        if (response.status() == null)
            return Verdict.UNKNOWN;

        return switch (response.status().id()) {
            case 3 -> Verdict.ACCEPTED;
            case 4 -> Verdict.WRONG_ANSWER;
            case 5 -> Verdict.TIME_LIMIT_EXCEEDED;
            case 6 -> Verdict.COMPILE_ERROR;
            case 7, 8, 9, 10, 11, 12 -> Verdict.RUNTIME_ERROR;
            case 1, 2 -> Verdict.PROCESSING;
            default -> Verdict.UNKNOWN;
        };
    }

    private Submission buildSubmission(
            InterviewSession session,
            Question question,
            SubmissionRequest request,
            SupportedLanguage lang,
            Judge0SubmissionResponse judge0Response,
            Verdict verdict) {

        Submission s = new Submission();
        s.setSession(session);
        s.setQuestion(question);
        s.setLanguage(lang.getKey());
        s.setSourceCode(request.sourceCode());
        s.setVerdict(verdict);
        s.setStdout(truncate(judge0Response.stdout(), 4096));
        s.setStderr(truncate(judge0Response.stderr(), 4096));
        s.setCompileOutput(truncate(judge0Response.compileOutput(), 4096));

        if (judge0Response.time() != null) {
            try {
                // Judge0 returns time in seconds as a string; convert to ms
                s.setRuntimeMs(Double.parseDouble(judge0Response.time()) * 1000.0);
            } catch (NumberFormatException ex) {
                log.warn("Could not parse Judge0 time value: {}", judge0Response.time());
            }
        }
        s.setMemoryKb(judge0Response.memory());
        return s;
    }

    @SuppressWarnings("unchecked")
    private String extractStdin(Map<String, Object> hiddenTestCases) {
        if (hiddenTestCases == null)
            return null;
        Object stdin = hiddenTestCases.get("stdin");
        return stdin != null ? stdin.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private String extractExpectedOutput(Map<String, Object> hiddenTestCases) {
        if (hiddenTestCases == null)
            return null;
        Object expected = hiddenTestCases.get("expectedOutput");
        return expected != null ? expected.toString() : null;
    }

    private String truncate(String value, int maxLength) {
        if (value == null)
            return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
