package com.server.concert_reservation.api.concert.domain.model.dto;

import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.infrastructure.types.ReservationStatus;

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
