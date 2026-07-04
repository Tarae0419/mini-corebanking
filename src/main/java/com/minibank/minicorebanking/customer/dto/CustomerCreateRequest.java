package com.minibank.minicorebanking.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CustomerCreateRequest(

        @NotBlank(message = "이름은 필수입니다")
        String name,

        @NotNull(message = "생년월일은 필수입니다")
        LocalDate birthDate,

        @NotBlank(message = "전화번호는 필수입니다")
        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
        String phone,

        @Email(message = "이메일 형식이 올바르지 않습니다")
        String email
){}