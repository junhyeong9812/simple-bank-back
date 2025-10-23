package com.simplebank.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Account {
    private Long id;
    private Long userId;
    private String accountNumber;
    private Money balance;
    private AccountStatus status;

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }
}
