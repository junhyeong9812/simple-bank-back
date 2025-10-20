package com.simplebank.user.adapter.out.persistence;

import com.simplebank.user.application.port.out.LoadUserPort;
import com.simplebank.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements LoadUserPort {
    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> loadByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> loadById(Long userId) {
        return jpaRepository.findById(userId)
                .map(UserJpaEntity::toDomain);
    }

}
