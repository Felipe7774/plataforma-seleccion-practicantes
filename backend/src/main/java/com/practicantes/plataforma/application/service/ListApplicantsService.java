package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantPage;
import com.practicantes.plataforma.domain.model.ApplicantSearchCriteria;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListApplicantsService {

    private final ApplicantRepository applicantRepository;

    public ListApplicantsService(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    public List<Applicant> list() {
        return applicantRepository.findAllByCreatedAtDescending();
    }

    public ApplicantPage list(ApplicantSearchCriteria criteria) {
        return applicantRepository.findPage(criteria);
    }
}

