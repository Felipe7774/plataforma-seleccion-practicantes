package com.practicantes.plataforma.infrastructure.persistence.entity;

import com.practicantes.plataforma.domain.model.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RefreshTokenJpaEntity() {}

    private RefreshTokenJpaEntity(RefreshToken refreshToken) {
        this.id = refreshToken.id();
        this.userId = refreshToken.userId();
        this.tokenHash = refreshToken.tokenHash();
        this.expiresAt = refreshToken.expiresAt();
        this.revoked = refreshToken.revoked();
        this.createdAt = refreshToken.createdAt();
    }

    public static RefreshTokenJpaEntity fromDomain(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(refreshToken);
    }

    public RefreshToken toDomain() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }
}

