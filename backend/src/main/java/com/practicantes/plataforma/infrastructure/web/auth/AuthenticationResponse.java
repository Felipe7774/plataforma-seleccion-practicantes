package com.practicantes.plataforma.infrastructure.web.auth;

import com.practicantes.plataforma.application.result.AuthenticationResult;
import java.time.Instant;

public record AuthenticationResponse(
        String accessToken, Instant accessTokenExpiresAt, String refreshToken) {

    static AuthenticationResponse from(AuthenticationResult result) {
        return new AuthenticationResponse(
                result.accessToken(), result.accessTokenExpiresAt(), result.refreshToken());
    }
}
