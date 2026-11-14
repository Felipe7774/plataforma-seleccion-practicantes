package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.ApplicantJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataApplicantRepository extends JpaRepository<ApplicantJpaEntity, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    List<ApplicantJpaEntity> findAllByOrderByCreatedAtDesc();
}

