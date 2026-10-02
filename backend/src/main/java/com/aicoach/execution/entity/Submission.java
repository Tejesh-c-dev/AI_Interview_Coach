package com.aicoach.execution.entity;

import com.aicoach.orchestrator.entity.InterviewSession;
import com.aicoach.questionbank.entity.Question;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Persisted record of a user's code submission and its execution result. */
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 32)
    private String language;

    // Stored server-side only — not returned in client responses.
    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Verdict verdict;

    /** Execution time in milliseconds as reported by Judge0. */
    @Column(name = "runtime_ms")
    private Double runtimeMs;

    /** Memory usage in kilobytes as reported by Judge0. */
    @Column(name = "memory_kb")
    private Integer memoryKb;

    @Column(columnDefinition = "TEXT")
    private String stdout;

    @Column(columnDefinition = "TEXT")
    private String stderr;

    @Column(name = "compile_output", columnDefinition = "TEXT")
    private String compileOutput;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }

    public InterviewSession getSession() { return session; }
    public void setSession(InterviewSession session) { this.session = session; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

    public Verdict getVerdict() { return verdict; }
    public void setVerdict(Verdict verdict) { this.verdict = verdict; }

    public Double getRuntimeMs() { return runtimeMs; }
    public void setRuntimeMs(Double runtimeMs) { this.runtimeMs = runtimeMs; }

    public Integer getMemoryKb() { return memoryKb; }
    public void setMemoryKb(Integer memoryKb) { this.memoryKb = memoryKb; }

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public String getCompileOutput() { return compileOutput; }
    public void setCompileOutput(String compileOutput) { this.compileOutput = compileOutput; }

    public Instant getCreatedAt() { return createdAt; }
}
