package com.aicoach.execution.controller;

import com.aicoach.execution.dto.SubmissionRequest;
import com.aicoach.execution.dto.SubmissionResponse;
import com.aicoach.execution.service.ExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for code submission and execution.
 *
 * <p>All Judge0 communication is server-side; this controller never proxies
 * raw Judge0 responses or credentials.
 */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final ExecutionService executionService;

    public SubmissionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    /**
     * Submit source code for a coding question in an active session.
     *
     * @param request     the submission payload (sessionId, questionId, sourceCode, language)
     * @param userDetails the authenticated principal injected by Spring Security
     * @return the normalised execution result
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse submit(
            @Valid @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return executionService.submit(request, userDetails.getUsername());
    }
}
