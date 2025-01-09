package com.server.concert_reservation.common.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConcertErrorCode implements ErrorCode {
    RESERVING_ONLY(HttpStatus.BAD_REQUEST, "임시 예약된 좌석만 확정할 수 있습니다."),
    CAN_NOT_RESERVE_SEAT(HttpStatus.BAD_REQUEST, "예약 가능한 좌석이 아닙니다."),
    CAN_NOT_RESERVE_DATE(HttpStatus.BAD_REQUEST, "예약 가능한 날짜가 아닙니다."),
    IS_NOT_TEMPORARY_RESERVATION(HttpStatus.FORBIDDEN, "임시 예약을 하셔야 결제가 가능합니다."),
    PAYMENT_ONLY_FOR_TEMP_RESERVATION(HttpStatus.BAD_REQUEST, "임시 예약 상태인 경우만 결제 완료 처리할 수 있습니다."),
    CANCEL_ONLY_FOR_TEMP_RESERVATION(HttpStatus.BAD_REQUEST, "임시 예약 상태인 경우만 취소할 수 있습니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트를 찾을 수 없습니다."),
    CONCERT_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트 일정이 존재하지 않습니다."),
    CONCERT_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석이 존재하지 않습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 내역이 존재하지 않습니다."),
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
