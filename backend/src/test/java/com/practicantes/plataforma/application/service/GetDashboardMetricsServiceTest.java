package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.practicantes.plataforma.domain.model.ApplicantMetrics;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetDashboardMetricsServiceTest {

    @Mock private ApplicantRepository applicantRepository;

    @Test
    @DisplayName("CP-40 returns totals grouped by status and career")
    void returnsMetrics() {
        ApplicantMetrics metrics = new ApplicantMetrics(
                10,
                Map.of(
                        ApplicantStatus.PENDING, 6L,
                        ApplicantStatus.VIABLE, 3L,
                        ApplicantStatus.NOT_VIABLE, 1L),
                Map.of("Ingeniería de Sistemas", 7L, "Diseño", 3L));
        given(applicantRepository.metrics()).willReturn(metrics);

        ApplicantMetrics result = new GetDashboardMetricsService(applicantRepository).get();

        assertThat(result.total()).isEqualTo(10);
        assertThat(result.byStatus().get(ApplicantStatus.PENDING)).isEqualTo(6L);
        assertThat(result.byCareer().get("Ingeniería de Sistemas")).isEqualTo(7L);
    }

    @Test
    @DisplayName("CP-41 returns empty metrics without errors")
    void returnsEmptyMetrics() {
        ApplicantMetrics metrics = new ApplicantMetrics(0, Map.of(), Map.of());
        given(applicantRepository.metrics()).willReturn(metrics);

        ApplicantMetrics result = new GetDashboardMetricsService(applicantRepository).get();

        assertThat(result.total()).isZero();
        assertThat(result.byStatus()).isEmpty();
        assertThat(result.byCareer()).isEmpty();
    }
}
