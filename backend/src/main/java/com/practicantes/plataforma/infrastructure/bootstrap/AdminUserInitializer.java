package com.practicantes.plataforma.infrastructure.bootstrap;

import com.practicantes.plataforma.domain.model.Role;
import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(
        prefix = "app.bootstrap-admin",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AdminUserInitializer {

    @Bean
    ApplicationRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username}") String username,
            @Value("${app.bootstrap-admin.password}") String password) {
        return arguments -> {
            if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                throw new IllegalStateException(
                        "APP_ADMIN_USERNAME and APP_ADMIN_PASSWORD must be configured");
            }

            if (!userRepository.existsByUsername(username)) {
                userRepository.save(User.create(username, passwordEncoder.encode(password), Role.ADMIN));
            }
        };
    }
}

