package com.aicoach.auth.controller;

import com.aicoach.auth.dto.RegisterRequest;
import com.aicoach.auth.dto.RegisterResponse;
import com.aicoach.auth.dto.LoginRequest;
import com.aicoach.auth.dto.LoginResponse;
import com.aicoach.auth.dto.UserResponse;
import com.aicoach.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

//Handles authentication endpoints.
@RestController
@RequestMapping("/api/auth") // url like /api/auth/register, /api/auth/login, /api/auth/me
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Creates an account and returns the created response payload.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Authenticates a user and returns the login response payload.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Retrieves the current authenticated user's information.
    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}
