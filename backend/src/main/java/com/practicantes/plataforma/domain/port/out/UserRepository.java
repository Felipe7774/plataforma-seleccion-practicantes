package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}

