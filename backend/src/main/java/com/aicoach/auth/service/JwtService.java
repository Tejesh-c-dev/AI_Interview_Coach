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
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expirationMillis
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured");
        }
        if (expirationMillis <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION must be greater than zero");
        }
        try {
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_SECRET must be a valid Base64 secret of at least 256 bits", exception);
        }
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String subject) {
        Date issuedAt = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + expirationMillis))
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

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

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
