package com.server.concert_reservation.application.concert.dto;

import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResult(Long id,
                                Long userId,
                                List<Long> seatIds,
                                int totalPrice,
                                ReservationStatus status,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {

    public static ReservationResult from(ReservationInfo reservation) {
        return new ReservationResult(reservation.id(),
                reservation.userId(),
                reservation.seatIds(),
                reservation.totalPrice(),
                reservation.status(),
                reservation.createdAt(),
                reservation.updatedAt());
    }
}
