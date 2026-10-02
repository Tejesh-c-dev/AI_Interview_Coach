package com.aicoach.progress.entity;

import com.aicoach.auth.entity.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "topic_progress",
        uniqueConstraints = @UniqueConstraint(name = "topic_progress_user_topic_unique",
                columnNames = {"user_id", "topic"}))
public class TopicProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "successful_attempts", nullable = false)
    private int successfulAttempts;

    @Column(nullable = false)
    private double accuracy;

    @Column(name = "last_practiced", nullable = false)
    private Instant lastPracticed;

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public int getAttempts() { return attempts; }
    public int getSuccessfulAttempts() { return successfulAttempts; }
    public double getAccuracy() { return accuracy; }
    public Instant getLastPracticed() { return lastPracticed; }

    public void recordAttempt(boolean successful, Instant practicedAt) {
        attempts++;
        if (successful) {
            successfulAttempts++;
        }
        accuracy = attempts == 0 ? 0.0 : (successfulAttempts * 100.0) / attempts;
        lastPracticed = practicedAt;
    }
}
