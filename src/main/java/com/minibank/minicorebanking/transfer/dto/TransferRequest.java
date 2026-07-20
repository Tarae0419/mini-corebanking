package com.minibank.minicorebanking.transfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
        @NotBlank(message = "보내는 계좌의 번호는 필수입니다.")
        String fromAccountNo,

        @NotBlank(message = "받는 계좌의 번호는 필수입니다.")
        String toAccountNo,

        @NotNull(message = "금액은 필수입니다.")
        @Positive(message = "금액은 0보다 커야 합니다.")
        Long amount,

        @NotBlank(message = "멱등성 키는 필수입니다.")
        String idempotencyKey
) {
}
