package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcertSchedule {
    private Long id;
    private Long concertId;
    private int remainTicket;
    private LocalDateTime reservationStartAt;
    private LocalDateTime reservationEndAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConcertSchedule of(Long id, Long concertId, int remainTicket,
                                     LocalDateTime reservationStartAt, LocalDateTime reservationEndAt,
                                     LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        return new ConcertSchedule(id, concertId, remainTicket, reservationStartAt, reservationEndAt, createdAt, updatedAt);
    }

    public void isAvailableReservePeriod(LocalDateTime dateTime) {
        if (dateTime.isBefore(reservationStartAt) || dateTime.isAfter(reservationEndAt)) {
            throw new CustomException(ConcertErrorCode.CAN_NOT_RESERVE_DATE);
        }
    }

}
