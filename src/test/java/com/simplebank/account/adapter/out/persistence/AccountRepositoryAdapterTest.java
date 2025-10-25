package com.simplebank.account.adapter.out.persistence;

import com.simplebank.account.domain.Account;
import com.simplebank.account.domain.AccountStatus;
import com.simplebank.account.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("AccountRepositoryAdapter 테스트")
class AccountRepositoryAdapterTest {

    @Autowired
    private AccountJpaRepository jpaRepository;

    private AccountRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AccountRepositoryAdapter(jpaRepository);
    }

    @Test
    @DisplayName("")
    void loadById_success() {
        //Given
        AccountJpaEntity entity = jpaRepository.builder()
                .userId(1L)
                .accountNumber("123456789")
                .balance(new BigDecimal("10000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
        AccountJpaEntity saved = jpaRepository.save(entity);

        //When
        Optional<Account> result = adapter.loadById(saved.getId());

        //Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().getAccountNumber()).isEqualTo("123456789");
        assertThat(result.get().getBalance().getAmount()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(result.get().getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

}