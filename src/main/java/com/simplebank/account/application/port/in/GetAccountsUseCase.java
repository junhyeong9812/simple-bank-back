package com.simplebank.account.application.port.in;

import com.simplebank.account.application.port.in.dto.AccountInfo;

import java.util.List;

public interface GetAccountsUseCase {
    List<AccountInfo> execute(Long userId);
}
