package com.practicantes.plataforma.domain.model;

public record ApplicantSearchCriteria(
        ApplicantStatus status, String career, String query, int page, int size) {

    public ApplicantSearchCriteria {
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
    }
}
