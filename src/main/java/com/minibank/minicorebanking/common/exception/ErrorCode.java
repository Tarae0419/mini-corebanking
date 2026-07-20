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
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "CU002", "존재하지 않는 고객입니다"),

    // Account
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "AC001", "존재하지 않는 계좌입니다"),
    ALREADY_CLOSED(HttpStatus.CONFLICT, "AC002", "이미 해지된 계좌입니다"),
    BALANCE_REMAINING(HttpStatus.CONFLICT, "AC003", "잔액이 남아 있어 해지할 수 없습니다"),
    ACCOUNT_NO_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AC004", "계좌번호 생성에 실패했습니다"),
    ACCOUNT_NOT_ACTIVE(HttpStatus.CONFLICT, "AC005", "활성 상태가 아닌 계좌입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.CONFLICT, "AC006", "잔액이 부족합니다");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
