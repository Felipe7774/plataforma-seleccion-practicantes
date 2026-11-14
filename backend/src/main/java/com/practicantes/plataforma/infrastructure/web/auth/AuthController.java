package com.practicantes.plataforma.infrastructure.web.auth;

import com.practicantes.plataforma.application.command.LoginCommand;
import com.practicantes.plataforma.application.command.RefreshSessionCommand;
import com.practicantes.plataforma.application.result.AuthenticationResult;
import com.practicantes.plataforma.application.service.AuthenticationService;
import com.practicantes.plataforma.infrastructure.security.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Inicio, renovación y cierre de sesión")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RateLimitService rateLimitService;

    public AuthController(
            AuthenticationService authenticationService, RateLimitService rateLimitService) {
        this.authenticationService = authenticationService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping(value = "/login", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Iniciar sesión", description = "Devuelve un access token JWT y un refresh token de un solo uso.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Autenticación correcta",
                content = @Content(
                        schema = @Schema(implementation = AuthenticationResponse.class),
                        examples = @ExampleObject(
                                value = """
                                {"accessToken":"eyJhbGciOiJIUzI1NiJ9...","accessTokenExpiresAt":"2026-11-14T17:15:00Z","refreshToken":"token-de-renovacion"}
                                """))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos", content = @Content),
        @ApiResponse(responseCode = "429", description = "Límite de intentos por minuto superado", content = @Content)
    })
    public AuthenticationResponse login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        rateLimitService.check("login", servletRequest.getRemoteAddr());
        AuthenticationResult result = authenticationService.login(
                new LoginCommand(request.username(), request.password()));
        return AuthenticationResponse.from(result);
    }

    @PostMapping(value = "/refresh", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renovar sesión", description = "Rota un refresh token vigente y devuelve un nuevo par de tokens.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sesión renovada", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido, vencido o revocado", content = @Content)
    })
    public AuthenticationResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResult result = authenticationService.refresh(
                new RefreshSessionCommand(request.refreshToken()));
        return AuthenticationResponse.from(result);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token enviado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Sesión cerrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
        @ApiResponse(responseCode = "401", description = "JWT ausente, vencido o inválido", content = @Content)
    })
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authenticationService.logout(request.refreshToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
