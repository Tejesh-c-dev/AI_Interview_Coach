package com.aicoach.orchestrator.entity;

import com.aicoach.questionbank.entity.Question;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "interview_session_questions",
        uniqueConstraints = @UniqueConstraint(name = "session_question_unique", columnNames = {"session_id", "question_id"}))
public class SessionQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_order", nullable = false)
    private int questionOrder;

    public void setSession(InterviewSession session) { this.session = session; }
    public void setQuestion(Question question) { this.question = question; }
    public void setQuestionOrder(int questionOrder) { this.questionOrder = questionOrder; }
}
