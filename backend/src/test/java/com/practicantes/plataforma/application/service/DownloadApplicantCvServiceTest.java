package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.practicantes.plataforma.application.result.DownloadedCv;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.FileStorage;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DownloadApplicantCvServiceTest {

    @Mock private GetApplicantService getApplicantService;
    @Mock private FileStorage fileStorage;

    @Test
    void returnsApplicantCvWithOriginalFilename() {
        UUID applicantId = UUID.randomUUID();
        byte[] content = "%PDF-1.7\n".getBytes(StandardCharsets.US_ASCII);
        Applicant applicant = new Applicant(
                applicantId,
                "Laura Gómez",
                "laura@correo.com",
                "Ingeniería de Sistemas",
                8,
                "cvs/laura.pdf",
                "laura-cv.pdf",
                ApplicantStatus.PENDING,
                Instant.parse("2026-11-14T17:00:00Z"),
                Instant.parse("2026-11-14T17:00:00Z"));
        given(getApplicantService.get(applicantId)).willReturn(applicant);
        given(fileStorage.load("cvs/laura.pdf")).willReturn(Optional.of(content));

        DownloadedCv result = new DownloadApplicantCvService(getApplicantService, fileStorage)
                .download(applicantId);

        assertThat(result.originalFilename()).isEqualTo("laura-cv.pdf");
        assertThat(result.content()).containsExactly(content);
    }
}
