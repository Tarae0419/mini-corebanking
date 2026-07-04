package com.minibank.minicorebanking.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), LocalDateTime.now());
    }

    public static  ErrorResponse of(ErrorCode errorCode, String detailMessage){
        return new ErrorResponse(errorCode.getCode(), detailMessage, LocalDateTime.now());
    }
}
