package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.StatusChangeWithUser;
import java.time.Instant;
import java.util.UUID;

public record StatusChangeResponse(
        UUID id,
        ApplicantStatus previousStatus,
        ApplicantStatus newStatus,
        String comment,
        String changedBy,
        Instant changedAt) {

    static StatusChangeResponse from(StatusChangeWithUser change) {
        return new StatusChangeResponse(
                change.statusChange().id(),
                change.statusChange().previousStatus(),
                change.statusChange().newStatus(),
                change.statusChange().comment(),
                change.changedByUsername(),
                change.statusChange().changedAt());
    }
}
