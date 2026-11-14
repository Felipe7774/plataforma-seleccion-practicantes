package com.practicantes.plataforma.infrastructure.persistence.adapter;

import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import com.practicantes.plataforma.infrastructure.persistence.entity.UserJpaEntity;
import com.practicantes.plataforma.infrastructure.persistence.repository.SpringDataUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = repository.save(UserJpaEntity.fromDomain(user));
        return entity.toDomain();
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username).map(UserJpaEntity::toDomain);
    }
}

