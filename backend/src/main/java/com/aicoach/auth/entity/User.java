package com.aicoach.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistence model for an application user.
 *
 * <p>
 * The table is named {@code users} because {@code user} is a reserved word
 * in PostgreSQL.
 * </p>
 **/
@Entity
@Table(name = "users")
public class User {

    // Database identifier for the user.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Use UUIDs for unique identifiers.
    @Column(name = "id", nullable = false, updatable = false) // Unique identifier for the user, generated automatically
                                                              // by the database.
    private UUID id; // Unique identifier for the user, generated automatically by the database.

    // Display name shown in the application.
    @Column(name = "name", nullable = false)
    private String name;

    // Normalized, unique email used to identify the account.
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // BCrypt-hashed account password.
    @Column(name = "password", nullable = false)
    private String password;

    /** Time when the user record was first persisted. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Sets the creation time when persistence first creates the entity. */
    @PrePersist // Called before the entity is persisted to the database.
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
