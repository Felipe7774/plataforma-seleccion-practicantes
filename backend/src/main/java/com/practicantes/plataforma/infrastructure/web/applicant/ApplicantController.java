package com.practicantes.plataforma.infrastructure.web.applicant;

import com.practicantes.plataforma.application.command.RegisterApplicantCommand;
import com.practicantes.plataforma.application.service.ChangeApplicantStatusService;
import com.practicantes.plataforma.application.service.GetApplicantService;
import com.practicantes.plataforma.application.service.ListApplicantsService;
import com.practicantes.plataforma.application.service.RegisterApplicantService;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.infrastructure.security.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Postulantes", description = "Registro y revisión de postulantes")
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

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Registrar un postulante",
            description = "Ruta pública. Recibe los datos del postulante y su hoja de vida en PDF de máximo 5 MB.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Postulante registrado con estado PENDING",
                content = @Content(
                        schema = @Schema(implementation = ApplicantResponse.class),
                        examples = @ExampleObject(
                                value = """
                                {"id":"9b7fb35f-4979-4c72-b345-0fe6b6f7c801","fullName":"Laura Gómez","email":"laura@correo.com","career":"Ingeniería de Sistemas","semester":8,"status":"PENDING","createdAt":"2026-11-14T17:00:00Z"}
                                """))),
        @ApiResponse(
                responseCode = "400",
                description = "Datos o archivo PDF inválidos",
                content = @Content(
                        mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        examples = @ExampleObject(
                                value = """
                                {"title":"Bad Request","status":400,"detail":"La hoja de vida debe ser un PDF"}
                                """))),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado", content = @Content),
        @ApiResponse(responseCode = "413", description = "El archivo supera el tamaño permitido", content = @Content),
        @ApiResponse(responseCode = "429", description = "Límite de registros por minuto superado", content = @Content)
    })
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar postulantes", description = "Devuelve los postulantes registrados.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Listado de postulantes",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicantResponse.class)))),
        @ApiResponse(responseCode = "401", description = "JWT ausente, vencido o inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol autorizado", content = @Content)
    })
    public java.util.List<ApplicantResponse> list() {
        return listApplicantsService.list().stream().map(ApplicantResponse::from).toList();
    }

    @GetMapping(value = "/{applicantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Consultar un postulante", description = "Obtiene el detalle de un postulante por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle del postulante", content = @Content(schema = @Schema(implementation = ApplicantDetailResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT ausente, vencido o inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Postulante no encontrado", content = @Content)
    })
    public ApplicantDetailResponse get(@PathVariable java.util.UUID applicantId) {
        return ApplicantDetailResponse.from(getApplicantService.get(applicantId));
    }

    @PatchMapping(value = "/{applicantId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar el estado de un postulante", description = "Actualiza el estado a VIABLE o NOT_VIABLE.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(schema = @Schema(implementation = ApplicantDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
        @ApiResponse(responseCode = "401", description = "JWT ausente, vencido o inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol autorizado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Postulante no encontrado", content = @Content)
    })
    public ApplicantDetailResponse changeStatus(
            @PathVariable java.util.UUID applicantId,
            @Valid @RequestBody ChangeApplicantStatusRequest request) {
        return ApplicantDetailResponse.from(
                changeApplicantStatusService.change(applicantId, request.status()));
    }
}

