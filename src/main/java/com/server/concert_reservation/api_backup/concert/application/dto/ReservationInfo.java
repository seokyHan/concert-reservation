package com.server.concert_reservation.api_backup.concert.application.dto;

import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationInfo(Long id,
                              Long userId,
                              List<ConcertSeat> seatIds,
                              int totalPrice,
                              ReservationStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt
) {

    public static ReservationInfo of(Reservation savedReservation, List<ConcertSeat> savedConcertSeatList) {
        return new ReservationInfo(savedReservation.getId(),
                savedReservation.getUserId(),
                savedConcertSeatList,
                savedReservation.getTotalPrice(),
                savedReservation.getStatus(),
                savedReservation.getCreatedAt(),
                savedReservation.getUpdatedAt());
    }
}
