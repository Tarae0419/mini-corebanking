package com.minibank.minicorebanking.account.dto;

import com.minibank.minicorebanking.account.entity.Account;
import com.minibank.minicorebanking.account.entity.AccountStatus;
import com.minibank.minicorebanking.account.entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNo,
        AccountType accountType,
        Long balance,
        AccountStatus status,
        Long dailyTransferLimit,
        BigDecimal interestRate,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account){
        return new AccountResponse(
                account.getId(), account.getAccountNo(), account.getAccountType(),
                account.getBalance(), account.getStatus(), account.getDailyTransferLimit(),
                account.getInterestRate(), account.getCreatedAt()
        );
    }
}
