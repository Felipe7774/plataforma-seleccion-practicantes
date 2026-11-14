package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.application.command.RegisterApplicantCommand;
import com.practicantes.plataforma.application.service.ChangeApplicantStatusService;
import com.practicantes.plataforma.application.service.GetApplicantService;
import com.practicantes.plataforma.application.service.ListApplicantsService;
import com.practicantes.plataforma.application.service.RegisterApplicantService;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.infrastructure.security.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final RateLimitService rateLimitService;

    public ApplicantController(
            RegisterApplicantService registerApplicantService,
            ListApplicantsService listApplicantsService,
            GetApplicantService getApplicantService,
            ChangeApplicantStatusService changeApplicantStatusService,
            RateLimitService rateLimitService) {
        this.registerApplicantService = registerApplicantService;
        this.listApplicantsService = listApplicantsService;
        this.getApplicantService = getApplicantService;
        this.changeApplicantStatusService = changeApplicantStatusService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicantResponse> register(
            @Valid @ModelAttribute RegisterApplicantRequest request,
            HttpServletRequest servletRequest) throws IOException {
        rateLimitService.check("registration", servletRequest.getRemoteAddr());
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
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public java.util.List<ApplicantResponse> list() {
        return listApplicantsService.list().stream().map(ApplicantResponse::from).toList();
    }

    @GetMapping("/{applicantId}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ApplicantDetailResponse get(@PathVariable java.util.UUID applicantId) {
        return ApplicantDetailResponse.from(getApplicantService.get(applicantId));
    }

    @PatchMapping("/{applicantId}/status")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ApplicantDetailResponse changeStatus(
            @PathVariable java.util.UUID applicantId,
            @Valid @RequestBody ChangeApplicantStatusRequest request) {
        return ApplicantDetailResponse.from(
                changeApplicantStatusService.change(applicantId, request.status()));
    }
}

