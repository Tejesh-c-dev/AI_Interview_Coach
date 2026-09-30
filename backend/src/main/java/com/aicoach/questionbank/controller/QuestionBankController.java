package com.aicoach.questionbank.controller;

import com.aicoach.questionbank.dto.QuestionResponse;
import com.aicoach.questionbank.dto.QuestionSelectionRequest;
import com.aicoach.questionbank.service.QuestionBankService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionBankController {
    private final QuestionBankService service;

    public QuestionBankController(QuestionBankService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> findQuestions(
            @RequestParam String track,
            @RequestParam String difficulty,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(service.findQuestions(track, difficulty, topic, type));
    }

    @PostMapping("/select")
    public ResponseEntity<QuestionResponse> selectQuestion(
            @Valid @RequestBody QuestionSelectionRequest request) {
        return ResponseEntity.ok(service.selectQuestion(request));
    }
}
