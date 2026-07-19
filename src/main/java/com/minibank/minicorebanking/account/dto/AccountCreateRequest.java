package com.minibank.minicorebanking.account.dto;

import com.minibank.minicorebanking.account.entity.AccountType;
import jakarta.validation.constraints.NotNull;

public record AccountCreateRequest(
        @NotNull(message = "고객 id는 필수입니다")
        Long customerId,

        @NotNull(message = "계좌 유형은 필수입니다.")
        AccountType accountType
){}
