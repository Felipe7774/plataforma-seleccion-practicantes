package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantMetrics;
import com.practicantes.plataforma.domain.model.ApplicantPage;
import com.practicantes.plataforma.domain.model.ApplicantSearchCriteria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicantRepository {
    Applicant save(Applicant applicant);

    boolean existsByEmail(String email);

    Optional<Applicant> findById(UUID id);

    List<Applicant> findAllByCreatedAtDescending();

    ApplicantPage findPage(ApplicantSearchCriteria criteria);

    ApplicantMetrics metrics();
}

