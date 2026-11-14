package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.exception.InvalidApplicantStatusException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ChangeApplicantStatusService {

    private final GetApplicantService getApplicantService;
    private final ApplicantRepository applicantRepository;
    private final Clock clock;

    public ChangeApplicantStatusService(
            GetApplicantService getApplicantService,
            ApplicantRepository applicantRepository,
            Clock clock) {
        this.getApplicantService = getApplicantService;
        this.applicantRepository = applicantRepository;
        this.clock = clock;
    }

    public Applicant change(UUID applicantId, ApplicantStatus newStatus) {
        if (newStatus == ApplicantStatus.PENDING) {
            throw new InvalidApplicantStatusException();
        }
        Applicant applicant = getApplicantService.get(applicantId);
        return applicantRepository.save(applicant.withStatus(newStatus, clock.instant()));
    }
}

