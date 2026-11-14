package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.practicantes.plataforma.application.command.RegisterApplicantCommand;
import com.practicantes.plataforma.application.exception.DuplicateApplicantEmailException;
import com.practicantes.plataforma.application.exception.InvalidCvException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.domain.model.StoredFile;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.domain.port.out.FileStorage;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterApplicantServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-14T17:00:00Z"), ZoneOffset.UTC);

    @Mock private ApplicantRepository applicantRepository;
    @Mock private FileStorage fileStorage;

    @Test
    @DisplayName("CP-01 registers a valid applicant with pending status")
    void registersValidApplicant() {
        RegisterApplicantService service = new RegisterApplicantService(
                applicantRepository, fileStorage, FIXED_CLOCK);
        given(applicantRepository.existsByEmail("laura@correo.com")).willReturn(false);
        given(fileStorage.save(any(CvFile.class)))
                .willReturn(new StoredFile("cvs/123.pdf", "hoja-de-vida.pdf"));
        given(applicantRepository.save(any(Applicant.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Applicant applicant = service.register(validCommand());

        assertThat(applicant.status()).isEqualTo(ApplicantStatus.PENDING);
        assertThat(applicant.email()).isEqualTo("laura@correo.com");
        verify(fileStorage).save(any(CvFile.class));
        ArgumentCaptor<Applicant> captor = ArgumentCaptor.forClass(Applicant.class);
        verify(applicantRepository).save(captor.capture());
        assertThat(captor.getValue().createdAt()).isEqualTo(FIXED_CLOCK.instant());
    }

    @Test
    @DisplayName("CP-05 rejects an email that is already registered before storing the file")
    void rejectsDuplicateEmailBeforeStoringFile() {
        RegisterApplicantService service = new RegisterApplicantService(
                applicantRepository, fileStorage, FIXED_CLOCK);
        given(applicantRepository.existsByEmail("laura@correo.com")).willReturn(true);

        assertThatThrownBy(() -> service.register(validCommand()))
                .isInstanceOf(DuplicateApplicantEmailException.class);

        verify(fileStorage, never()).save(any(CvFile.class));
        verify(applicantRepository, never()).save(any(Applicant.class));
    }

    @Test
    @DisplayName("CP-10 rejects a file with a PDF extension but no PDF signature")
    void rejectsFileWithoutPdfSignature() {
        RegisterApplicantService service = new RegisterApplicantService(
                applicantRepository, fileStorage, FIXED_CLOCK);
        given(applicantRepository.existsByEmail("laura@correo.com")).willReturn(false);
        CvFile renamedImage = new CvFile(
                "hoja.pdf", "application/pdf", "not-a-pdf".getBytes(StandardCharsets.US_ASCII));

        assertThatThrownBy(() -> service.register(commandWithCv(renamedImage)))
                .isInstanceOf(InvalidCvException.class)
                .hasMessage("La hoja de vida debe ser un PDF");

        verify(fileStorage, never()).save(any(CvFile.class));
    }

    private RegisterApplicantCommand validCommand() {
        CvFile cv = new CvFile(
                "hoja-de-vida.pdf",
                "application/pdf",
                "%PDF-1.7\n".getBytes(StandardCharsets.US_ASCII));
        return commandWithCv(cv);
    }

    private RegisterApplicantCommand commandWithCv(CvFile cv) {
        return new RegisterApplicantCommand(
                "Laura Gómez", "Laura@Correo.com", "Ingeniería de Sistemas", 8, cv);
    }
}

