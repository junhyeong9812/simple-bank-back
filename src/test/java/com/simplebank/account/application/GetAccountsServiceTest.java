package com.simplebank.account.application;

import com.simplebank.account.application.port.in.dto.AccountInfo;
import com.simplebank.account.application.port.out.LoadAccountPort;
import com.simplebank.account.domain.Account;
import com.simplebank.account.domain.AccountStatus;
import com.simplebank.account.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAccountsService 테스트")
class GetAccountsServiceTest {
    @Mock
    private LoadAccountPort loadAccountPort;

    @InjectMocks
    private GetAccountsService getAccountsService;

    @Test
    @DisplayName("사용자 ID로 계좌 목록 조회 성공")
    void getAccounts_success() {
        //Given
        Long userId = 1L;
        List<Account> accounts = List.of(
                Account.builder()
                        .id(1L)
                        .userId(userId)
                        .accountNumber("1234567890")
                        .balance(new Money(new BigDecimal("10000.00")))
                        .status(AccountStatus.ACTIVE)
                        .build()
                ,
                Account.builder()
                        .id(2L)
                        .userId(userId)
                        .accountNumber("0987654321")
                        .balance(new Money(new BigDecimal("50000.00")))
                        .status(AccountStatus.ACTIVE)
                        .build()
        );

        when(loadAccountPort.loadByUserId(userId))
                .thenReturn(accounts);

        //When
        List<AccountInfo> result = getAccountsService.execute(userId);

        //Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAccountId()).isEqualTo(1L);
        assertThat(result.get(0).getAccountNumber()).isEqualTo("1234567890");
        assertThat(result.get(0).getBalance()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        assertThat(result.get(1).getAccountId()).isEqualTo(2L);
        assertThat(result.get(1).getAccountNumber()).isEqualTo("0987654321");
        assertThat(result.get(1).getBalance()).isEqualTo(new BigDecimal("50000.00"));
        verify(loadAccountPort).loadByUserId(userId);

    }
}