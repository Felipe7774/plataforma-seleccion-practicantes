package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.ApplicantDetails;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record ApplicantDetailResponse(
        UUID id,
        String fullName,
        String email,
        String career,
        int semester,
        String cvOriginalName,
        ApplicantStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<StatusChangeResponse> history) {

    static ApplicantDetailResponse from(ApplicantDetails details) {
        var applicant = details.applicant();
        return new ApplicantDetailResponse(
                applicant.id(),
                applicant.fullName(),
                applicant.email(),
                applicant.career(),
                applicant.semester(),
                applicant.cvOriginalName(),
                applicant.status(),
                applicant.createdAt(),
                applicant.updatedAt(),
                details.history().stream().map(StatusChangeResponse::from).toList());
    }
}

