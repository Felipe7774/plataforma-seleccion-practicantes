package com.practicantes.plataforma.domain.model;

import java.util.List;

public record ApplicantPage(
        List<Applicant> items, long totalElements, int totalPages, int page, int size) {}
