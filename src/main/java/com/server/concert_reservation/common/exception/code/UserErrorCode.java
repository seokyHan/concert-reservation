package com.server.concert_reservation.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID 입니다."),
    INVALID_POINT(HttpStatus.BAD_REQUEST, "충전하려는 포인트는 0보다 커야합니다."),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "충전하려는 포인트는 0보다 커야합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;


    @Override
    public String getCode() {
        return name();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
