package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.domain.model.ApplicantMetrics;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import org.springframework.stereotype.Service;

@Service
public class GetDashboardMetricsService {

    private final ApplicantRepository applicantRepository;

    public GetDashboardMetricsService(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    public ApplicantMetrics get() {
        return applicantRepository.metrics();
    }
}
