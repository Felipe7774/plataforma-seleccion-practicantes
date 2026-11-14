package com.practicantes.plataforma.application.result;

import java.time.Instant;

public record AuthenticationResult(
        String accessToken, Instant accessTokenExpiresAt, String refreshToken) {}
