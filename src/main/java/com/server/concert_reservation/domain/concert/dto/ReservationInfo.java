package com.server.concert_reservation.domain.concert.dto;

import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationInfo(Long id,
                              Long userId,
                              Long concertScheduleId,
                              List<Long> seatIds,
                              int totalPrice,
                              ReservationStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt
) {

    public static ReservationInfo from(Reservation reservation) {
        return new ReservationInfo(reservation.getId(),
                reservation.getUserId(),
                reservation.getConcertScheduleId(),
                reservation.getSeatIds(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt());
    }


}
