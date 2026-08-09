package com.aicoach.auth.repository;

import com.aicoach.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email address to look up
     * @return the matching user, or {@link Optional#empty()} if none exists
     */
    Optional<User> findByEmail(String email);
}
