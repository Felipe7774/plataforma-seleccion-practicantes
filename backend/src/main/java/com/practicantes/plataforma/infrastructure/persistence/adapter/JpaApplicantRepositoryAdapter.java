package com.practicantes.plataforma.infrastructure.persistence.adapter;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.infrastructure.persistence.entity.ApplicantJpaEntity;
import com.practicantes.plataforma.infrastructure.persistence.repository.SpringDataApplicantRepository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaApplicantRepositoryAdapter implements ApplicantRepository {

    private final SpringDataApplicantRepository repository;

    public JpaApplicantRepositoryAdapter(SpringDataApplicantRepository repository) {
        this.repository = repository;
    }

    @Override
    public Applicant save(Applicant applicant) {
        ApplicantJpaEntity entity = repository.save(ApplicantJpaEntity.fromDomain(applicant));
        return entity.toDomain();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<Applicant> findById(UUID id) {
        return repository.findById(id).map(ApplicantJpaEntity::toDomain);
    }

    @Override
    public List<Applicant> findAllByCreatedAtDescending() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(ApplicantJpaEntity::toDomain)
                .toList();
    }
}

