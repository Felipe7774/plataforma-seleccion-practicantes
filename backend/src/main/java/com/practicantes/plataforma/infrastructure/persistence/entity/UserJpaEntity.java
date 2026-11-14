package com.practicantes.plataforma.infrastructure.persistence.entity;

import com.practicantes.plataforma.domain.model.Role;
import com.practicantes.plataforma.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected UserJpaEntity() {}

    private UserJpaEntity(User user) {
        this.id = user.id();
        this.username = user.username();
        this.passwordHash = user.passwordHash();
        this.role = user.role();
        this.active = user.active();
        this.createdAt = user.createdAt();
    }

    public static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(user);
    }

    public User toDomain() {
        return new User(id, username, passwordHash, role, active, createdAt);
    }
}

