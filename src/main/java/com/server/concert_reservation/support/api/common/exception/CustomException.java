package com.server.concert_reservation.support.api.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorType errorType;
    private String[] values;

    public CustomException(ErrorType errorType, String... values) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.values = values;
    }
}

