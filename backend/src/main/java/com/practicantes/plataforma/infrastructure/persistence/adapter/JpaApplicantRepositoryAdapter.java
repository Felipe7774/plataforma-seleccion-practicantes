package com.practicantes.plataforma.infrastructure.persistence.adapter;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantMetrics;
import com.practicantes.plataforma.domain.model.ApplicantPage;
import com.practicantes.plataforma.domain.model.ApplicantSearchCriteria;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.infrastructure.persistence.entity.ApplicantJpaEntity;
import com.practicantes.plataforma.infrastructure.persistence.repository.SpringDataApplicantRepository;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public ApplicantPage findPage(ApplicantSearchCriteria criteria) {
        Specification<ApplicantJpaEntity> specification = Specification.allOf(
                hasStatus(criteria.status()), hasCareer(criteria.career()), matchesQuery(criteria.query()));
        Page<ApplicantJpaEntity> page = repository.findAll(
                specification,
                PageRequest.of(criteria.page(), criteria.size(), Sort.by(Sort.Direction.DESC, "createdAt")));
        return new ApplicantPage(
                page.getContent().stream().map(ApplicantJpaEntity::toDomain).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize());
    }

    @Override
    public ApplicantMetrics metrics() {
        EnumMap<ApplicantStatus, Long> byStatus = new EnumMap<>(ApplicantStatus.class);
        for (ApplicantStatus status : ApplicantStatus.values()) {
            byStatus.put(status, 0L);
        }
        repository.countByStatus().forEach(row -> byStatus.put((ApplicantStatus) row[0], (Long) row[1]));

        LinkedHashMap<String, Long> byCareer = new LinkedHashMap<>();
        repository.countByCareer().forEach(row -> byCareer.put((String) row[0], (Long) row[1]));
        return new ApplicantMetrics(byStatus.values().stream().mapToLong(Long::longValue).sum(), byStatus, byCareer);
    }

    private Specification<ApplicantJpaEntity> hasStatus(ApplicantStatus status) {
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("status"), status);
    }

    private Specification<ApplicantJpaEntity> hasCareer(String career) {
        return (root, query, builder) -> career == null || career.isBlank()
                ? null
                : builder.equal(builder.lower(root.get("career")), career.trim().toLowerCase());
    }

    private Specification<ApplicantJpaEntity> matchesQuery(String query) {
        return (root, criteriaQuery, builder) -> {
            if (query == null || query.isBlank()) {
                return null;
            }
            String pattern = "%" + query.trim().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("fullName")), pattern),
                    builder.like(builder.lower(root.get("email")), pattern));
        };
    }
}

