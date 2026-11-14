package com.practicantes.plataforma.infrastructure.security;

import com.practicantes.plataforma.domain.model.AccessToken;
import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.AccessTokenIssuer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final Duration accessTokenDuration;

    public JwtAccessTokenIssuer(
            JwtEncoder jwtEncoder,
            Clock clock,
            @Value("${app.security.access-token-minutes}") long accessTokenMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.accessTokenDuration = Duration.ofMinutes(accessTokenMinutes);
    }

    @Override
    public AccessToken issue(User user) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(accessTokenDuration);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.username())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("uid", user.id().toString())
                .claim("role", user.role().name())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AccessToken(value, expiresAt);
    }
}
