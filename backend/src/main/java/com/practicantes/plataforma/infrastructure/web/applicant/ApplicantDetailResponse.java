package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import java.time.Instant;
import java.util.UUID;

public record ApplicantDetailResponse(
        UUID id,
        String fullName,
        String email,
        String career,
        int semester,
        String cvOriginalName,
        ApplicantStatus status,
        Instant createdAt,
        Instant updatedAt) {

    static ApplicantDetailResponse from(Applicant applicant) {
        return new ApplicantDetailResponse(
                applicant.id(),
                applicant.fullName(),
                applicant.email(),
                applicant.career(),
                applicant.semester(),
                applicant.cvOriginalName(),
                applicant.status(),
                applicant.createdAt(),
                applicant.updatedAt());
    }
}

