package com.aicoach.progress.controller;

import com.aicoach.progress.dto.*;
import com.aicoach.progress.service.ProgressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/{userId}")
    public List<TopicProgressResponse> getProgress(@PathVariable UUID userId,
                                                    org.springframework.security.core.Authentication authentication) {
        return progressService.getProgress(userId, authentication.getName());
    }

    @GetMapping("/{userId}/history")
    public List<InterviewHistoryResponse> getHistory(@PathVariable UUID userId,
                                                      org.springframework.security.core.Authentication authentication) {
        return progressService.getHistory(userId, authentication.getName());
    }

    @GetMapping("/{userId}/dashboard")
    public ProgressDashboardResponse getDashboard(@PathVariable UUID userId,
                                                   org.springframework.security.core.Authentication authentication) {
        return progressService.getDashboard(userId, authentication.getName());
    }
}
