package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);
}

