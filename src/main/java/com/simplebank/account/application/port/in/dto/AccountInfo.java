package com.simplebank.account.application.port.in.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountInfo {
    private final Long accountId;
    private final String accountNumber;
    private final BigDecimal balance;
    private final String status;
}
