package com.minibank.minicorebanking.account.entity;

import com.minibank.minicorebanking.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class AccountTest {

    @Test
    @DisplayName("출금하면 잔액이 감소한다")
    void withdraw_success(){
        Account account = Account.builder()
                .accountNo("110-000000001")
                .accountType(AccountType.CHECKING)
                .build();
        account.deposit(100_000L);
        account.withdraw(30_000L);
        assertThat(account.getBalance()).isEqualTo(70_000L);
    }

    @Test
    @DisplayName("잔액보다 큰 금액을 출금하면 예외가 발생한다")
    void withdraw_insufficientBalance() {
        // given
        Account account = Account.builder()
                .accountNo("110-000000001")
                .accountType(AccountType.CHECKING)
                .build();
        account.deposit(50_000L);

        // when & then
        assertThatThrownBy(() -> account.withdraw(100_000L))
                .isInstanceOf(BusinessException.class);
    }
}
