package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserJpaEntity> findByUsernameIgnoreCase(String username);
}

