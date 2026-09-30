package com.aicoach.orchestrator.controller;

import com.aicoach.orchestrator.dto.*;
import com.aicoach.orchestrator.service.InterviewSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class InterviewSessionController {
    private final InterviewSessionService service;

    public InterviewSessionController(InterviewSessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> start(@Valid @RequestBody StartSessionRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.start(request, authentication.getName()));
    }

    @GetMapping("/{id}/next-question")
    public ResponseEntity<NextQuestionResponse> nextQuestion(@PathVariable UUID id,
                                                               Authentication authentication) {
        return ResponseEntity.ok(service.nextQuestion(id, authentication.getName()));
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<SessionResponse> finish(@PathVariable UUID id,
                                                   Authentication authentication) {
        return ResponseEntity.ok(service.finish(id, authentication.getName()));
    }
}
