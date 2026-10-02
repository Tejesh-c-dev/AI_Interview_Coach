package com.aicoach.progress.dto;

import java.util.List;

public record ProgressDashboardResponse(
        long totalSessions,
        long completedSessions,
        List<InterviewHistoryResponse> recentSessions,
        List<TopicProgressResponse> progress,
        List<Double> recentAccuracyTrend) {
}
