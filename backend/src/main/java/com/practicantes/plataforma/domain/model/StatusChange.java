package com.practicantes.plataforma.domain.model;

import java.time.Instant;
import java.util.UUID;

public record StatusChange(
        UUID id,
        UUID applicantId,
        ApplicantStatus previousStatus,
        ApplicantStatus newStatus,
        String comment,
        UUID changedBy,
        Instant changedAt) {}

