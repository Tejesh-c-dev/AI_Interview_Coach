package com.aicoach.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/** Creates and validates access tokens for the stateless API. */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String secret, // injected from application.properties
            @Value("${app.jwt.expiration}") long expirationMillis // injected from application.properties
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured");
        }
        if (expirationMillis <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION must be greater than zero");
        }
        try {
            // The secret must be a Base64-encoded string of at least 256 bits (32 bytes)
            // for HMAC-SHA algorithms.
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_SECRET must be a valid Base64 secret of at least 256 bits", exception);
        }
        this.expirationMillis = expirationMillis;
    }

    // Generates a JWT token with the given subject (usually the user's email or
    // ID).
    public String generateToken(String subject) {
        Date issuedAt = new Date();
        return Jwts.builder() // starts building the JWT
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + expirationMillis)) // sets the expiration time
                .signWith(signingKey) // signs the JWT with the secret key
                .compact(); // builds the JWT and serializes it to a compact, URL-safe string
    }

    // Extracts the subject (usually the user's email or ID) from a JWT token.
    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    // Validates a JWT token by checking its signature and expiration.
    public boolean isValid(String token) {
        try {
            return extractSubject(token) != null;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    // Parses the claims from a JWT token. Throws an exception if the token is
    // invalid.
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
