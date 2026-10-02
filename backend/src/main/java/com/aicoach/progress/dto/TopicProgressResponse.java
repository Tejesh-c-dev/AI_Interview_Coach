package com.aicoach.progress.dto;

import com.aicoach.progress.entity.TopicProgress;

import java.time.Instant;

public record TopicProgressResponse(String topic, int attempts, double accuracy, Instant lastPracticed) {
    public static TopicProgressResponse from(TopicProgress progress) {
        return new TopicProgressResponse(progress.getTopic(), progress.getAttempts(),
                progress.getAccuracy(), progress.getLastPracticed());
    }
}
