package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.exception.ApplicantNotFoundException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetApplicantService {

    private final ApplicantRepository applicantRepository;

    public GetApplicantService(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    public Applicant get(UUID applicantId) {
        return applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ApplicantNotFoundException(applicantId));
    }
}

