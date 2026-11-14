package com.practicantes.plataforma.domain.model;

import java.util.Map;

public record ApplicantMetrics(
        long total, Map<ApplicantStatus, Long> byStatus, Map<String, Long> byCareer) {}
