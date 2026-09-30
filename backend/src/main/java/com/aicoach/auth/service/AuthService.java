package com.aicoach.auth.service;

import com.aicoach.auth.dto.RegisterRequest;
import com.aicoach.auth.dto.RegisterResponse;
import com.aicoach.auth.dto.LoginRequest;
import com.aicoach.auth.dto.LoginResponse;
import com.aicoach.auth.dto.UserResponse;
import com.aicoach.auth.entity.User;
import com.aicoach.auth.exception.DuplicateEmailException;
import com.aicoach.auth.exception.InvalidCredentialsException;
import com.aicoach.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Business logic for authentication-related operations.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     *
     * <p>The email is normalized (trimmed and lower-cased) before both the
     * duplicate lookup and the insert, so {@code "  User@Example.COM "} and
     * {@code "user@example.com"} are treated as the same address.</p>
     *
     * @param request the validated registration payload
     * @return the registration response
     * @throws DuplicateEmailException if the email is already registered
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            // saveAndFlush surfaces the unique-constraint violation inside this
            // transaction, so concurrent registrations are caught even when two
            // requests pass the findByEmail check above at the same time.
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException("Email is already registered");
        }

        return new RegisterResponse("User registered successfully");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new LoginResponse(jwtService.generateToken(user.getEmail()), "Bearer",
                jwtService.getExpirationMillis());
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
