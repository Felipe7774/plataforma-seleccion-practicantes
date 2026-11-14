package com.practicantes.plataforma.infrastructure.web.dashboard;

import com.practicantes.plataforma.domain.model.ApplicantMetrics;
import java.util.List;

public record DashboardMetricsResponse(
        long total, List<StatusMetricResponse> byStatus, List<CareerMetricResponse> byCareer) {

    static DashboardMetricsResponse from(ApplicantMetrics metrics) {
        return new DashboardMetricsResponse(
                metrics.total(),
                metrics.byStatus().entrySet().stream()
                        .map(entry -> new StatusMetricResponse(entry.getKey(), entry.getValue()))
                        .toList(),
                metrics.byCareer().entrySet().stream()
                        .map(entry -> new CareerMetricResponse(entry.getKey(), entry.getValue()))
                        .toList());
    }
}
