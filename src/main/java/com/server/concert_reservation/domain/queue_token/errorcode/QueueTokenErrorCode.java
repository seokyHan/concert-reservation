package com.server.concert_reservation.domain.queue_token.errorcode;

import com.server.concert_reservation.support.api.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.logging.LogLevel.WARN;

@Getter
@RequiredArgsConstructor
public enum QueueTokenErrorCode implements ErrorType {
    ALREADY_ACTIVATED(HttpStatus.CONFLICT, "이미 활성화된 대기열 토큰 입니다.", WARN),
    CAN_NOT_ACTIVE_TOKEN_EXPIRED(HttpStatus.CONFLICT, "만료된 대기열 토큰은 활성화할 수 없습니다.", WARN),
    TOKEN_NOT_ACTIVATED(HttpStatus.UNAUTHORIZED, "대기열 토큰이 활성 상태가 아닙니다.", WARN),
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "대기열 토큰이 만료되었습니다.", WARN),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "대기열 토큰이 존재하지 않습니다.", WARN),
    ;

    private final HttpStatus status;
    private final String message;
    private final LogLevel logLevel;


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