package com.aicoach.progress.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopicProgressTest {
    @Test
    void calculatesDeterministicAccuracyFromAttempts() {
        TopicProgress progress = new TopicProgress();
        Instant first = Instant.parse("2026-01-01T00:00:00Z");
        Instant second = Instant.parse("2026-01-02T00:00:00Z");

        progress.recordAttempt(false, first);
        progress.recordAttempt(true, second);
        progress.recordAttempt(true, second);

        assertEquals(3, progress.getAttempts());
        assertEquals(2, progress.getSuccessfulAttempts());
        assertEquals(200.0 / 3.0, progress.getAccuracy());
        assertEquals(second, progress.getLastPracticed());
    }
}
