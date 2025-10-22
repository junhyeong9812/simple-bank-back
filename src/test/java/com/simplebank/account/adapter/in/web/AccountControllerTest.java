package com.simplebank.account.adapter.in.web;

import com.simplebank.account.application.port.in.GetAccountsUseCase;
import com.simplebank.account.application.port.in.dto.AccountInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@DisplayName("AccountController 테스트")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAccountsUseCase getAccountsUseCase;

    @Test
    @DisplayName("GET /api/accounts/user/{userId} - 사용자 계좌 목록 조회 성공")
    void getAccounts_success() throws Exception {
        //Given
        Long userId = 1L;
        List<AccountInfo> accounts = List.of(
                new AccountInfo(1L, "1234567890", new BigDecimal("10000.00"), "ACTIVE"),
                new AccountInfo(2L, "0987654321", new BigDecimal("50000.00"), "ACTIVE")
        );

        when(getAccountsUseCase.execute(userId))
                .thenReturn(accounts);

        //When & Then
        mockMvc.perform(get("/api/accounts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1L))
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(10000.00))
                .andExpect(jsonPath("$[0]status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].accountId").value(2L))
                .andExpect(jsonPath("$[1].balance").value(50000.00));

    }

    @Test
    @DisplayName("GET /api/accounts/user/{userId}")
    void getAccounts_empty() throws Exception {
        //Given
        Long userId = 999L;
        List<AccountInfo> emptyAccounts = List.of();

        when(getAccountsUseCase.execute(userId))
                .thenReturn(emptyAccounts);

        //When & Then
        mockMvc.perform(get("/api/accounts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.lengh()").value(0));
    }

}