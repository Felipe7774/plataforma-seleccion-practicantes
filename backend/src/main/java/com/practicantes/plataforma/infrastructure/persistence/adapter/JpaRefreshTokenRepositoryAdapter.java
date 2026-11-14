package com.practicantes.plataforma.infrastructure.persistence.adapter;

import com.practicantes.plataforma.domain.model.RefreshToken;
import com.practicantes.plataforma.domain.port.out.RefreshTokenRepository;
import com.practicantes.plataforma.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.practicantes.plataforma.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository repository;

    public JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return repository.save(RefreshTokenJpaEntity.fromDomain(refreshToken)).toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(RefreshTokenJpaEntity::toDomain);
    }
}
