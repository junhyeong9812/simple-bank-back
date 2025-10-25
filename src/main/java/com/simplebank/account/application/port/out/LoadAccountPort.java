package com.simplebank.account.application.port.out;

import  com.simplebank.account.domain.Account;

import java.util.List;
import java.util.Optional;

public interface LoadAccountPort {
    Optional<Account> loadById(Long accountId);
    List<Account> loadByUserId(Long userId);
}
