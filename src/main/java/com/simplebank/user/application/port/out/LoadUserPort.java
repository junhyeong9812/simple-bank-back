package com.simplebank.user.application.port.out;

import com.simplebank.user.domain.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadByUsername(String username);
    Optional<User> loadById(Long userId);
}
