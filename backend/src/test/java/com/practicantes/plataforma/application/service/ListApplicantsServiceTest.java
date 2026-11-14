package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.practicantes.plataforma.domain.model.ApplicantPage;
import com.practicantes.plataforma.domain.model.ApplicantSearchCriteria;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListApplicantsServiceTest {

    @Mock private ApplicantRepository applicantRepository;

    @Test
    @DisplayName("CP-21 to CP-23 forwards pagination, filters and search criteria")
    void forwardsSearchCriteria() {
        ApplicantSearchCriteria criteria = new ApplicantSearchCriteria(
                ApplicantStatus.PENDING, "Ingeniería de Sistemas", "laura", 1, 5);
        ApplicantPage page = new ApplicantPage(List.of(), 6, 2, 1, 5);
        given(applicantRepository.findPage(criteria)).willReturn(page);

        ApplicantPage result = new ListApplicantsService(applicantRepository).list(criteria);

        assertThat(result).isEqualTo(page);
        verify(applicantRepository).findPage(criteria);
    }
}
