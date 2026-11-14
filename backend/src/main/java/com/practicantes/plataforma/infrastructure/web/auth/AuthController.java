package com.practicantes.plataforma.infrastructure.web.auth;

import com.practicantes.plataforma.application.command.LoginCommand;
import com.practicantes.plataforma.application.command.RefreshSessionCommand;
import com.practicantes.plataforma.application.result.AuthenticationResult;
import com.practicantes.plataforma.application.service.AuthenticationService;
import com.practicantes.plataforma.infrastructure.security.RateLimitService;
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
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RateLimitService rateLimitService;

    public AuthController(
            AuthenticationService authenticationService, RateLimitService rateLimitService) {
        this.authenticationService = authenticationService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        rateLimitService.check("login", servletRequest.getRemoteAddr());
        AuthenticationResult result = authenticationService.login(
                new LoginCommand(request.username(), request.password()));
        return AuthenticationResponse.from(result);
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResult result = authenticationService.refresh(
                new RefreshSessionCommand(request.refreshToken()));
        return AuthenticationResponse.from(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authenticationService.logout(request.refreshToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
