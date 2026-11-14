package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.Applicant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicantRepository {
    Applicant save(Applicant applicant);

    boolean existsByEmail(String email);

    Optional<Applicant> findById(UUID id);

    List<Applicant> findAllByCreatedAtDescending();
}

