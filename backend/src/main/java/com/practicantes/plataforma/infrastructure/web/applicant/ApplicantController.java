package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.application.command.RegisterApplicantCommand;
import com.practicantes.plataforma.application.service.ChangeApplicantStatusService;
import com.practicantes.plataforma.application.service.GetApplicantService;
import com.practicantes.plataforma.application.service.ListApplicantsService;
import com.practicantes.plataforma.application.service.RegisterApplicantService;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.CvFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/applicants")
public class ApplicantController {

    private final RegisterApplicantService registerApplicantService;
    private final ListApplicantsService listApplicantsService;
    private final GetApplicantService getApplicantService;
    private final ChangeApplicantStatusService changeApplicantStatusService;

    public ApplicantController(
            RegisterApplicantService registerApplicantService,
            ListApplicantsService listApplicantsService,
            GetApplicantService getApplicantService,
            ChangeApplicantStatusService changeApplicantStatusService) {
        this.registerApplicantService = registerApplicantService;
        this.listApplicantsService = listApplicantsService;
        this.getApplicantService = getApplicantService;
        this.changeApplicantStatusService = changeApplicantStatusService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicantResponse> register(
            @Valid @ModelAttribute RegisterApplicantRequest request) throws IOException {
        MultipartFile uploadedCv = request.cv();
        CvFile cv = uploadedCv == null
                ? null
                : new CvFile(
                        uploadedCv.getOriginalFilename(), uploadedCv.getContentType(), uploadedCv.getBytes());

        Applicant applicant = registerApplicantService.register(
                new RegisterApplicantCommand(
                        request.fullName(), request.email(), request.career(), request.semester(), cv));

        return ResponseEntity.created(URI.create("/api/v1/applicants/" + applicant.id()))
                .body(ApplicantResponse.from(applicant));
    }

    @GetMapping
    public java.util.List<ApplicantResponse> list() {
        return listApplicantsService.list().stream().map(ApplicantResponse::from).toList();
    }

    @GetMapping("/{applicantId}")
    public ApplicantDetailResponse get(@PathVariable java.util.UUID applicantId) {
        return ApplicantDetailResponse.from(getApplicantService.get(applicantId));
    }

    @PatchMapping("/{applicantId}/status")
    public ApplicantDetailResponse changeStatus(
            @PathVariable java.util.UUID applicantId,
            @Valid @RequestBody ChangeApplicantStatusRequest request) {
        return ApplicantDetailResponse.from(
                changeApplicantStatusService.change(applicantId, request.status()));
    }
}

