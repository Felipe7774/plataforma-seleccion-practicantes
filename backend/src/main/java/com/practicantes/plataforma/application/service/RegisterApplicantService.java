package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.command.RegisterApplicantCommand;
import com.practicantes.plataforma.application.exception.DuplicateApplicantEmailException;
import com.practicantes.plataforma.application.exception.InvalidCvException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.domain.model.StoredFile;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.domain.port.out.FileStorage;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RegisterApplicantService {

    private static final int MAX_CV_SIZE_BYTES = 5 * 1024 * 1024;
    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes();

    private final ApplicantRepository applicantRepository;
    private final FileStorage fileStorage;
    private final Clock clock;

    public RegisterApplicantService(
            ApplicantRepository applicantRepository, FileStorage fileStorage, Clock clock) {
        this.applicantRepository = applicantRepository;
        this.fileStorage = fileStorage;
        this.clock = clock;
    }

    public Applicant register(RegisterApplicantCommand command) {
        String normalizedEmail = command.email().trim().toLowerCase(Locale.ROOT);
        if (applicantRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateApplicantEmailException();
        }

        validatePdf(command.cv());
        StoredFile storedFile = fileStorage.save(command.cv());
        Instant now = clock.instant();

        Applicant applicant = new Applicant(
                UUID.randomUUID(),
                command.fullName().trim(),
                normalizedEmail,
                command.career().trim(),
                command.semester(),
                storedFile.key(),
                storedFile.originalFilename(),
                ApplicantStatus.PENDING,
                now,
                now);

        return applicantRepository.save(applicant);
    }

    private void validatePdf(CvFile cv) {
        if (cv == null || cv.content().length == 0) {
            throw new InvalidCvException("Adjunta tu hoja de vida en PDF");
        }
        if (cv.content().length > MAX_CV_SIZE_BYTES) {
            throw new InvalidCvException("El archivo no puede superar 5 MB");
        }
        if (!"application/pdf".equalsIgnoreCase(cv.contentType()) || !hasPdfSignature(cv.content())) {
            throw new InvalidCvException("La hoja de vida debe ser un PDF");
        }
    }

    private boolean hasPdfSignature(byte[] content) {
        if (content.length < PDF_SIGNATURE.length) {
            return false;
        }
        for (int index = 0; index < PDF_SIGNATURE.length; index++) {
            if (content[index] != PDF_SIGNATURE[index]) {
                return false;
            }
        }
        return true;
    }
}

