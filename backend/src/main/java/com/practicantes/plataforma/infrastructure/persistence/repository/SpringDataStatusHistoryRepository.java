package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.StatusHistoryJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStatusHistoryRepository
        extends JpaRepository<StatusHistoryJpaEntity, UUID> {
    List<StatusHistoryJpaEntity> findByApplicantIdOrderByChangedAtAsc(UUID applicantId);
}
