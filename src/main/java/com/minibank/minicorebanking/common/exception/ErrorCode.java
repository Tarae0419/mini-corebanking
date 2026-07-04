package com.minibank.minicorebanking.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다"),

    DUPLICATE_PHONE(HttpStatus.CONFLICT, "CU001", "이미 등록된 전화번호입니다"),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "CU002", "존재하지 않는 고객입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
