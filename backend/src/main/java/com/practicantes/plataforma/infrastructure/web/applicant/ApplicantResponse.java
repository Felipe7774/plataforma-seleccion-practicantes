package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import java.time.Instant;
import java.util.UUID;

public record ApplicantResponse(
        UUID id,
        String fullName,
        String email,
        String career,
        int semester,
        ApplicantStatus status,
        Instant createdAt) {

    static ApplicantResponse from(Applicant applicant) {
        return new ApplicantResponse(
                applicant.id(),
                applicant.fullName(),
                applicant.email(),
                applicant.career(),
                applicant.semester(),
                applicant.status(),
                applicant.createdAt());
    }
}

