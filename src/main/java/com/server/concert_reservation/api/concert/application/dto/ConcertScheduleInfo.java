package com.server.concert_reservation.api.concert.application.dto;

import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;

import java.time.LocalDateTime;

public record ConcertScheduleInfo(Long id,
                                  Long concertId,
                                  int remainTicket,
                                  LocalDateTime reservationStartAt,
                                  LocalDateTime reservationEndAt,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt
) {

    public static ConcertScheduleInfo of(ConcertSchedule concertSchedule) {
        return new ConcertScheduleInfo(concertSchedule.getId(),
                concertSchedule.getConcertId(),
                concertSchedule.getRemainTicket(),
                concertSchedule.getReservationStartAt(),
                concertSchedule.getReservationEndAt(),
                concertSchedule.getCreatedAt(),
                concertSchedule.getUpdatedAt());
    }
}
