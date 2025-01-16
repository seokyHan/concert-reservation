package com.server.concert_reservation.support.api.common.exception;

public record ErrorResponse(String code, String message) {

    public static ErrorResponse from(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
