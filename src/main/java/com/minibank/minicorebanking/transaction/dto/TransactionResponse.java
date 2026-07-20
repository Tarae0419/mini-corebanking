package com.minibank.minicorebanking.transaction.dto;

import com.minibank.minicorebanking.transaction.entity.TransactionHistory;
import com.minibank.minicorebanking.transaction.entity.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        TransactionType transactionType,
        Long amount,
        Long balanceAfter,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(TransactionHistory history){
        return new TransactionResponse(
                history.getId(),
                history.getTransactionType(),
                history.getAmount(),
                history.getBalanceAfter(),
                history.getCreatedAt()
        );
    }

}
