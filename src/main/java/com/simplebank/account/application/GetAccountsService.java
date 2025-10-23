package com.simplebank.account.application;

import com.simplebank.account.application.port.in.GetAccountsUseCase;
import com.simplebank.account.application.port.in.dto.AccountInfo;
import com.simplebank.account.application.port.out.LoadAccountPort;
import com.simplebank.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAccountsService implements GetAccountsUseCase {

    private final LoadAccountPort loadAccountPort;

    @Override
    public List<AccountInfo> execute(Long userId) {
        // 1. 사용자의 계좌 목록 조회
        List<Account> accounts = loadAccountPort.loadByUserId(userId);

        // 2.AccountInfo로 변환하여 반환
        return accounts.stream()
                .map(account -> new AccountInfo(
                        account.getId(),
                        account.getAccountNumber(),
                        account.getBalance().getAmount(),
                        account.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}
