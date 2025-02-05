package com.server.concert_reservation.application.concert.dto;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;

import java.time.LocalDateTime;

public record ConcertScheduleResult(Long id,
                                    Long concertId,
                                    int remainTicket,
                                    LocalDateTime reservationStartAt,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {

    public static ConcertScheduleResult from(ConcertScheduleInfo concertScheduleInfo) {
        return new ConcertScheduleResult(concertScheduleInfo.id(),
                concertScheduleInfo.concertId(),
                concertScheduleInfo.remainTicket(),
                concertScheduleInfo.reservationStartAt(),
                concertScheduleInfo.createdAt(),
                concertScheduleInfo.updatedAt());
    }
}
