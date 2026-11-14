package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.domain.model.ApplicantPage;
import java.util.List;

public record ApplicantPageResponse(
        List<ApplicantResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size) {

    static ApplicantPageResponse from(ApplicantPage page) {
        return new ApplicantPageResponse(
                page.items().stream().map(ApplicantResponse::from).toList(),
                page.totalElements(),
                page.totalPages(),
                page.page(),
                page.size());
    }
}
