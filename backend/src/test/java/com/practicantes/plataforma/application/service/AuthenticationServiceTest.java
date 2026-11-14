package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.practicantes.plataforma.application.command.LoginCommand;
import com.practicantes.plataforma.application.command.RefreshSessionCommand;
import com.practicantes.plataforma.application.exception.InvalidRefreshTokenException;
import com.practicantes.plataforma.application.result.AuthenticationResult;
import com.practicantes.plataforma.domain.model.AccessToken;
import com.practicantes.plataforma.domain.model.RefreshToken;
import com.practicantes.plataforma.domain.model.Role;
import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.AccessTokenIssuer;
import com.practicantes.plataforma.domain.port.out.RefreshTokenRepository;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-14T17:00:00Z"), ZoneOffset.UTC);

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private AccessTokenIssuer accessTokenIssuer;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("AUTH-01 emits an access token and stores only the refresh-token hash")
    void logsInAndStoresOnlyRefreshTokenHash() {
        AuthenticationService service = service();
        User user = activeUser();
        given(userRepository.findByUsername("admin")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("secret", user.passwordHash())).willReturn(true);
        given(accessTokenIssuer.issue(user)).willReturn(accessToken());
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        AuthenticationResult result = service.login(new LoginCommand(" ADMIN ", "secret"));

        assertThat(result.accessToken()).isEqualTo("signed-access-token");
        assertThat(result.refreshToken()).isNotBlank();
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken storedToken = tokenCaptor.getValue();
        assertThat(storedToken.tokenHash()).isEqualTo(sha256(result.refreshToken()));
        assertThat(storedToken.tokenHash()).isNotEqualTo(result.refreshToken());
        assertThat(storedToken.expiresAt()).isEqualTo(FIXED_CLOCK.instant().plusSeconds(7 * 86_400));
    }

    @Test
    @DisplayName("AUTH-02 rotates an active refresh token")
    void rotatesRefreshToken() {
        AuthenticationService service = service();
        String oldRawToken = "refresh-token-from-client";
        User user = activeUser();
        RefreshToken oldToken = new RefreshToken(
                UUID.randomUUID(), user.id(), sha256(oldRawToken), FIXED_CLOCK.instant().plusSeconds(60), false,
                FIXED_CLOCK.instant().minusSeconds(60));
        given(refreshTokenRepository.findByTokenHash(sha256(oldRawToken))).willReturn(Optional.of(oldToken));
        given(userRepository.findById(user.id())).willReturn(Optional.of(user));
        given(accessTokenIssuer.issue(user)).willReturn(accessToken());
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        AuthenticationResult result = service.refresh(new RefreshSessionCommand(oldRawToken));

        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, org.mockito.Mockito.times(2)).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getAllValues().getFirst().revoked()).isTrue();
        assertThat(tokenCaptor.getAllValues().get(1).revoked()).isFalse();
        assertThat(tokenCaptor.getAllValues().get(1).tokenHash()).isEqualTo(sha256(result.refreshToken()));
    }

    @Test
    @DisplayName("AUTH-03 rejects a revoked refresh token")
    void rejectsRevokedRefreshToken() {
        AuthenticationService service = service();
        String rawToken = "refresh-token-from-client";
        RefreshToken revokedToken = new RefreshToken(
                UUID.randomUUID(), UUID.randomUUID(), sha256(rawToken), FIXED_CLOCK.instant().plusSeconds(60), true,
                FIXED_CLOCK.instant().minusSeconds(60));
        given(refreshTokenRepository.findByTokenHash(sha256(rawToken))).willReturn(Optional.of(revokedToken));

        assertThatThrownBy(() -> service.refresh(new RefreshSessionCommand(rawToken)))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    private AuthenticationService service() {
        return new AuthenticationService(
                userRepository,
                refreshTokenRepository,
                accessTokenIssuer,
                passwordEncoder,
                FIXED_CLOCK,
                7);
    }

    private User activeUser() {
        return new User(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "admin",
                "$2a$10$hashed-password",
                Role.ADMIN,
                true,
                FIXED_CLOCK.instant());
    }

    private AccessToken accessToken() {
        return new AccessToken("signed-access-token", FIXED_CLOCK.instant().plusSeconds(900));
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
