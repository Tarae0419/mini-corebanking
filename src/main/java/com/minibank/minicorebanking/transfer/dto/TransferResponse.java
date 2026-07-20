package com.minibank.minicorebanking.transfer.dto;

import com.minibank.minicorebanking.transfer.entity.Transfer;
import com.minibank.minicorebanking.transfer.entity.TransferStatus;

import java.time.LocalDateTime;

public record TransferResponse(
    Long id,
    String fromAccountNo,
    String toAccountNo,
    Long amount,
    TransferStatus status,
    LocalDateTime createdAt,
    LocalDateTime completedAt
)
{
    public static TransferResponse from(Transfer transfer, String fromAccountNo, String toAccountNo) {
        return new TransferResponse(
                transfer.getId(),
                fromAccountNo,
                toAccountNo,
                transfer.getAmount(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getCompletedAt()
        );
    }
}
