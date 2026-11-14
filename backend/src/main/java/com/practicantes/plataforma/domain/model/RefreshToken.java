package com.practicantes.plataforma.domain.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        boolean revoked,
        Instant createdAt) {

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, true, createdAt);
    }
}

