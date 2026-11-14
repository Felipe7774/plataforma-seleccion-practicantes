package com.practicantes.plataforma.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.practicantes.plataforma.application.exception.TooManyRequestsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RateLimitServiceTest {

    @Test
    @DisplayName("SEC-01 rejects the request that exceeds the client operation limit")
    void rejectsRequestAboveConfiguredLimit() {
        RateLimitService rateLimitService = new RateLimitService(2);

        rateLimitService.check("login", "127.0.0.1");
        rateLimitService.check("login", "127.0.0.1");

        assertThatThrownBy(() -> rateLimitService.check("login", "127.0.0.1"))
                .isInstanceOf(TooManyRequestsException.class);
    }
}
