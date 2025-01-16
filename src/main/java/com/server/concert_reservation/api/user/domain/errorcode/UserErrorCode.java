package com.server.concert_reservation.api.user.domain.errorcode;

import com.server.concert_reservation.support.api.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.logging.LogLevel.WARN;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorType {
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID 입니다.", WARN),
    INVALID_POINT(HttpStatus.BAD_REQUEST, "충전하려는 포인트는 0보다 커야합니다.", WARN),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "포인트가 충분하지 않습니다.", WARN),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다.", WARN),
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
