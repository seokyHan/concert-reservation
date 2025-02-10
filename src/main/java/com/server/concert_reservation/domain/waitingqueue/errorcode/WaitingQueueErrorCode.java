package com.server.concert_reservation.domain.waitingqueue.errorcode;

import com.server.concert_reservation.interfaces.web.support.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WaitingQueueErrorCode implements ErrorType {
    WAITING_QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, "대기열 정보를 찾을 수 없습니다.", LogLevel.WARN),
    ACTIVE_QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND, "활성 대기열 정보를 찾을 수 없습니다.", LogLevel.WARN),
    WAITING_QUEUE_EXPIRED(HttpStatus.BAD_REQUEST, "대기열이 만료되었습니다.", LogLevel.WARN);

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