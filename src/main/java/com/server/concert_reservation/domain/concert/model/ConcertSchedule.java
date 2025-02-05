package com.server.concert_reservation.domain.concert.model;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ConcertSchedule of(Long id, Long concertId, int remainTicket,
                                     LocalDateTime reservationStartAt,
                                     LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        return new ConcertSchedule(id, concertId, remainTicket, reservationStartAt, createdAt, updatedAt);
    }

    public boolean isAvailableReservePeriod(LocalDateTime dateTime) {
        return dateTime.isAfter(reservationStartAt);
    }

}
