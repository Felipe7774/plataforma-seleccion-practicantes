package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantDetails;
import com.practicantes.plataforma.domain.model.StatusChangeWithUser;
import com.practicantes.plataforma.domain.port.out.StatusHistoryRepository;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetApplicantDetailsService {

    private final GetApplicantService getApplicantService;
    private final StatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;

    public GetApplicantDetailsService(
            GetApplicantService getApplicantService,
            StatusHistoryRepository statusHistoryRepository,
            UserRepository userRepository) {
        this.getApplicantService = getApplicantService;
        this.statusHistoryRepository = statusHistoryRepository;
        this.userRepository = userRepository;
    }

    public ApplicantDetails get(java.util.UUID applicantId) {
        Applicant applicant = getApplicantService.get(applicantId);
        List<StatusChangeWithUser> history = statusHistoryRepository
                .findByApplicantIdOrderByChangedAt(applicantId)
                .stream()
                .map(change -> new StatusChangeWithUser(
                        change,
                        userRepository.findById(change.changedBy())
                                .map(user -> user.username())
                                .orElse("Usuario eliminado")))
                .toList();
        return new ApplicantDetails(applicant, history);
    }
}
