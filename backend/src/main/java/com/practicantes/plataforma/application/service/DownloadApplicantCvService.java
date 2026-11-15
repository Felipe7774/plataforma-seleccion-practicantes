package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.exception.CvNotFoundException;
import com.practicantes.plataforma.application.result.DownloadedCv;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.port.out.FileStorage;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DownloadApplicantCvService {

    private final GetApplicantService getApplicantService;
    private final FileStorage fileStorage;

    public DownloadApplicantCvService(
            GetApplicantService getApplicantService, FileStorage fileStorage) {
        this.getApplicantService = getApplicantService;
        this.fileStorage = fileStorage;
    }

    public DownloadedCv download(UUID applicantId) {
        Applicant applicant = getApplicantService.get(applicantId);
        byte[] content = fileStorage.load(applicant.cvKey())
                .orElseThrow(CvNotFoundException::new);
        return new DownloadedCv(applicant.cvOriginalName(), content);
    }
}
