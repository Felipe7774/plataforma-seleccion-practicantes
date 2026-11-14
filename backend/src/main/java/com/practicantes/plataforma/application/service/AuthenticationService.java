package com.practicantes.plataforma.application.service;

import com.practicantes.plataforma.application.command.LoginCommand;
import com.practicantes.plataforma.application.command.RefreshSessionCommand;
import com.practicantes.plataforma.application.exception.InvalidCredentialsException;
import com.practicantes.plataforma.application.exception.InvalidRefreshTokenException;
import com.practicantes.plataforma.application.result.AuthenticationResult;
import com.practicantes.plataforma.domain.model.AccessToken;
import com.practicantes.plataforma.domain.model.RefreshToken;
import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.AccessTokenIssuer;
import com.practicantes.plataforma.domain.port.out.RefreshTokenRepository;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenIssuer accessTokenIssuer;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final Duration refreshTokenDuration;

    public AuthenticationService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            AccessTokenIssuer accessTokenIssuer,
            PasswordEncoder passwordEncoder,
            Clock clock,
            @Value("${app.security.refresh-token-days}") long refreshTokenDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenIssuer = accessTokenIssuer;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.refreshTokenDuration = Duration.ofDays(refreshTokenDays);
    }

    @Transactional
    public AuthenticationResult login(LoginCommand command) {
        String username = command.username().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByUsername(username).orElseThrow(InvalidCredentialsException::new);
        if (!user.active() || !passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult refresh(RefreshSessionCommand command) {
        RefreshToken previousToken = refreshTokenRepository.findByTokenHash(hash(command.refreshToken()))
                .filter(token -> !token.revoked())
                .filter(token -> token.expiresAt().isAfter(clock.instant()))
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepository.findById(previousToken.userId())
                .filter(User::active)
                .orElseThrow(InvalidRefreshTokenException::new);

        refreshTokenRepository.save(previousToken.revoke());
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(hash(refreshToken))
                .filter(token -> !token.revoked())
                .ifPresent(token -> refreshTokenRepository.save(token.revoke()));
    }

    private AuthenticationResult issueTokens(User user) {
        AccessToken accessToken = accessTokenIssuer.issue(user);
        String refreshToken = generateOpaqueToken();
        Instant now = clock.instant();
        refreshTokenRepository.save(new RefreshToken(
                UUID.randomUUID(),
                user.id(),
                hash(refreshToken),
                now.plus(refreshTokenDuration),
                false,
                now));
        return new AuthenticationResult(accessToken.value(), accessToken.expiresAt(), refreshToken);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
