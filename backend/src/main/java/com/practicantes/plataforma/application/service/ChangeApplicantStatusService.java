package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.exception.InvalidApplicantStatusException;
import com.practicantes.plataforma.application.exception.MissingStatusCommentException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.StatusChange;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.domain.port.out.NotificationSender;
import com.practicantes.plataforma.domain.port.out.StatusHistoryRepository;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import java.time.Clock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeApplicantStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeApplicantStatusService.class);

    private final GetApplicantService getApplicantService;
    private final ApplicantRepository applicantRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationSender notificationSender;
    private final Clock clock;

    public ChangeApplicantStatusService(
            GetApplicantService getApplicantService,
            ApplicantRepository applicantRepository,
            StatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository,
            NotificationSender notificationSender,
            Clock clock) {
        this.getApplicantService = getApplicantService;
        this.applicantRepository = applicantRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
        this.notificationSender = notificationSender;
        this.clock = clock;
    }

    @Transactional
    public Applicant change(UUID applicantId, ApplicantStatus newStatus, String comment, String username) {
        if (newStatus == ApplicantStatus.PENDING) {
            throw new InvalidApplicantStatusException();
        }
        String normalizedComment = normalizeComment(comment);
        if (newStatus == ApplicantStatus.NOT_VIABLE && normalizedComment == null) {
            throw new MissingStatusCommentException();
        }
        Applicant applicant = getApplicantService.get(applicantId);
        Applicant updatedApplicant = applicantRepository.save(applicant.withStatus(newStatus, clock.instant()));
        java.util.UUID changedBy = userRepository.findByUsername(username)
                .orElseThrow(IllegalStateException::new)
                .id();
        statusHistoryRepository.save(new StatusChange(
                java.util.UUID.randomUUID(),
                applicantId,
                applicant.status(),
                newStatus,
                normalizedComment,
                changedBy,
                clock.instant()));
        try {
            notificationSender.sendStatusChanged(
                    updatedApplicant.email(), updatedApplicant.fullName(), updatedApplicant.status());
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible programar la notificación para {}", updatedApplicant.email(), exception);
        }
        return updatedApplicant;
    }

    private String normalizeComment(String comment) {
        return comment == null || comment.isBlank() ? null : comment.trim();
    }
}

