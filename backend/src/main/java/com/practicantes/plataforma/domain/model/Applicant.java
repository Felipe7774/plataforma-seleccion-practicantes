package com.practicantes.plataforma.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Applicant(
        UUID id,
        String fullName,
        String email,
        String career,
        int semester,
        String cvKey,
        String cvOriginalName,
        ApplicantStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public Applicant {
        Objects.requireNonNull(id, "id is required");
        requireText(fullName, "fullName");
        requireText(email, "email");
        requireText(career, "career");
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("semester must be between 1 and 12");
        }
        requireText(cvKey, "cvKey");
        requireText(cvOriginalName, "cvOriginalName");
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    public Applicant withStatus(ApplicantStatus newStatus, Instant changedAt) {
        return new Applicant(
                id,
                fullName,
                email,
                career,
                semester,
                cvKey,
                cvOriginalName,
                newStatus,
                createdAt,
                changedAt);
    }
}

