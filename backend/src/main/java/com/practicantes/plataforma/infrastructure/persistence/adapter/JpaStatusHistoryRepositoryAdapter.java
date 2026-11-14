package com.practicantes.plataforma.infrastructure.persistence.adapter;

import com.practicantes.plataforma.domain.model.StatusChange;
import com.practicantes.plataforma.domain.port.out.StatusHistoryRepository;
import com.practicantes.plataforma.infrastructure.persistence.entity.StatusHistoryJpaEntity;
import com.practicantes.plataforma.infrastructure.persistence.repository.SpringDataStatusHistoryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaStatusHistoryRepositoryAdapter implements StatusHistoryRepository {

    private final SpringDataStatusHistoryRepository repository;

    public JpaStatusHistoryRepositoryAdapter(SpringDataStatusHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatusChange save(StatusChange statusChange) {
        return repository.save(StatusHistoryJpaEntity.fromDomain(statusChange)).toDomain();
    }

    @Override
    public List<StatusChange> findByApplicantIdOrderByChangedAt(UUID applicantId) {
        return repository.findByApplicantIdOrderByChangedAtAsc(applicantId).stream()
                .map(StatusHistoryJpaEntity::toDomain)
                .toList();
    }
}
