package com.server.concert_reservation.domain.concert.errorcode;

import com.server.concert_reservation.support.api.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.logging.LogLevel.WARN;

@Getter
@RequiredArgsConstructor
public enum ConcertErrorCode implements ErrorType {
    RESERVING_ONLY(HttpStatus.BAD_REQUEST, "임시 예약된 좌석만 확정할 수 있습니다.", WARN),
    CAN_NOT_RESERVE_SEAT(HttpStatus.BAD_REQUEST, "예약 가능한 좌석이 아닙니다.", WARN),
    CONCERT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 콘서트를 찾을 수 없습니다.", WARN),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "콘서트 일정이 존재하지 않습니다.", WARN),
    CONCERT_SEAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "좌석이 존재하지 않습니다.", WARN),
    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "예약 내역이 존재하지 않습니다.", WARN),
    PAYMENT_ONLY_FOR_TEMP_RESERVATION(HttpStatus.CONFLICT, "좌석 임시 예약을 먼저 하셔야 결제 가능합니다.", WARN),
    CANCEL_ONLY_FOR_TEMP_RESERVATION(HttpStatus.CONFLICT, "임시 예약 상태인 경우만 취소할 수 있습니다.", WARN),
    ALREADY_SOLD_SEAT(HttpStatus.CONFLICT, "이미 판매된 좌석 입니다.", WARN),
    IS_NOT_TEMPORARY_RESERVATION(HttpStatus.CONFLICT, "좌석 임시 예약을 먼저 하셔야 합니다.", WARN),
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
