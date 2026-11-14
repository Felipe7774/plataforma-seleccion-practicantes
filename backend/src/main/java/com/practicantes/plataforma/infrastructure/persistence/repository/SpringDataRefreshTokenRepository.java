package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);
}
