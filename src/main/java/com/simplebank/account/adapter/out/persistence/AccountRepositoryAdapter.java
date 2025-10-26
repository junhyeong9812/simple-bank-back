package com.simplebank.account.adapter.out.persistence;

import com.simplebank.account.application.port.out.LoadAccountPort;
import com.simplebank.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements LoadAccountPort{
    private final AccountJpaRepository jpaRepository;

    @Override
    public Optional<Account> loadById(Long accountId) {
        return jpaRepository.findById(accountId)
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public List<Account> loadByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(AccountJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}
