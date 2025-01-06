package com.server.concert_reservation.common.exception;

import com.server.concert_reservation.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private String[] values;

    public CustomException(ErrorCode errorCode, String... values) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.values = values;
    }
}

