package com.server.concert_reservation.interfaces.web.support.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorType errorType;
    private String[] values;

    public CustomException(ErrorType errorType, String... values) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.values = values;
    }
}

