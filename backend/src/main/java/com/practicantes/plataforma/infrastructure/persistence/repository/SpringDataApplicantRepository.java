package com.practicantes.plataforma.infrastructure.persistence.repository;

import com.practicantes.plataforma.infrastructure.persistence.entity.ApplicantJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataApplicantRepository
        extends JpaRepository<ApplicantJpaEntity, UUID>, JpaSpecificationExecutor<ApplicantJpaEntity> {
    boolean existsByEmailIgnoreCase(String email);

    List<ApplicantJpaEntity> findAllByOrderByCreatedAtDesc();

    @Query("select applicant.status, count(applicant) from ApplicantJpaEntity applicant group by applicant.status")
    List<Object[]> countByStatus();

    @Query("select applicant.career, count(applicant) from ApplicantJpaEntity applicant group by applicant.career order by applicant.career")
    List<Object[]> countByCareer();
}

