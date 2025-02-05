package com.server.concert_reservation.domain.concert.dto;

import com.server.concert_reservation.domain.concert.model.ConcertSchedule;

import java.time.LocalDateTime;

public record ConcertScheduleInfo(Long id,
                                  Long concertId,
                                  int remainTicket,
                                  LocalDateTime reservationStartAt,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt
) {

    public static ConcertScheduleInfo from(ConcertSchedule concertSchedule) {
        return new ConcertScheduleInfo(concertSchedule.getId(),
                concertSchedule.getConcertId(),
                concertSchedule.getRemainTicket(),
                concertSchedule.getReservationStartAt(),
                concertSchedule.getCreatedAt(),
                concertSchedule.getUpdatedAt());
    }
}
