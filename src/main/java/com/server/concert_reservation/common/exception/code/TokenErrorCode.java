package com.server.concert_reservation.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    ALREADY_ACTIVATED(HttpStatus.FORBIDDEN, "이미 활성화된 대기열 토큰 입니다."),
    CAN_NOT_ACTIVE_TOKEN_EXPIRED(HttpStatus.BAD_GATEWAY, "만료된 대기열 토큰은 활성화할 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "대기열 토큰이 만료되었습니다."),
    TOKEN_NOT_ACTIVATED(HttpStatus.FORBIDDEN, "대기열 토큰이 활성상태가 아닙니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "대기열 토큰이 존재하지 않습니다."),
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