package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.ApplicantStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeApplicantStatusRequest(
        @NotNull(message = "El estado es obligatorio") ApplicantStatus status) {}

