package com.aicoach.auth.service;

import com.aicoach.auth.dto.RegisterRequest;
import com.aicoach.auth.dto.RegisterResponse;
import com.aicoach.auth.entity.User;
import com.aicoach.auth.exception.DuplicateEmailException;
import com.aicoach.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for authentication-related operations.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        String email = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User();
        user.setName(request.name());
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
}
