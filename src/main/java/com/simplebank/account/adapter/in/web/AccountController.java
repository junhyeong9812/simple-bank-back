package com.simplebank.account.adapter.in.web;

import com.simplebank.account.application.port.in.GetAccountsUseCase;
import com.simplebank.account.application.port.in.dto.AccountInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final GetAccountsUseCase getAccountsUseCase;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountInfo>> getAccounts(@PathVariable Long userId) {
        List<AccountInfo> accounts = getAccountsUseCase.execute(userId);
        return ResponseEntity.ok(accounts);
    }
}
