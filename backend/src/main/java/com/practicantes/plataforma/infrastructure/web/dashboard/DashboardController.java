package com.practicantes.plataforma.infrastructure.web.dashboard;

import com.practicantes.plataforma.application.service.GetDashboardMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Métricas de las postulaciones")
public class DashboardController {

    private final GetDashboardMetricsService getDashboardMetricsService;

    public DashboardController(GetDashboardMetricsService getDashboardMetricsService) {
        this.getDashboardMetricsService = getDashboardMetricsService;
    }

    @GetMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Consultar métricas", description = "Devuelve totales por estado y carrera.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métricas disponibles", content = @Content(schema = @Schema(implementation = DashboardMetricsResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT ausente, vencido o inválido", content = @Content),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene un rol autorizado", content = @Content)
    })
    public DashboardMetricsResponse metrics() {
        return DashboardMetricsResponse.from(getDashboardMetricsService.get());
    }
}
