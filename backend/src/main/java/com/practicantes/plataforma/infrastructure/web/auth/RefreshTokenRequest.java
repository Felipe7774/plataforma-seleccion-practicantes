package com.practicantes.plataforma.infrastructure.web.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "El token de renovación es obligatorio") String refreshToken) {}
