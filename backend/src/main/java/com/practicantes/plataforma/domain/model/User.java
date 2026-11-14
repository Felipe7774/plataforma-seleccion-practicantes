package com.practicantes.plataforma.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String passwordHash,
        Role role,
        boolean active,
        Instant createdAt) {

    public User {
        Objects.requireNonNull(id, "id is required");
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash is required");
        }
        Objects.requireNonNull(role, "role is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public static User create(String username, String passwordHash, Role role) {
        return new User(UUID.randomUUID(), username, passwordHash, role, true, Instant.now());
    }
}

