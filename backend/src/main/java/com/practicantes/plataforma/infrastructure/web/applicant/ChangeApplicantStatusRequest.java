package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.ApplicantStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeApplicantStatusRequest(
        @NotNull(message = "El estado es obligatorio") ApplicantStatus status,
        @Size(max = 1000, message = "El comentario no puede superar 1000 caracteres") String comment) {}

