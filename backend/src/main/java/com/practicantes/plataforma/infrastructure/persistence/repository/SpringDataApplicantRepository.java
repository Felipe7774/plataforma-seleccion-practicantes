package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.ApplicantJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataApplicantRepository extends JpaRepository<ApplicantJpaEntity, UUID> {
    boolean existsByEmailIgnoreCase(String email);
}

