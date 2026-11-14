package com.practicantes.plataforma.infrastructure.web.dashboard;

import com.practicantes.plataforma.domain.model.ApplicantStatus;

public record StatusMetricResponse(ApplicantStatus status, long count) {}
