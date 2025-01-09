package com.server.concert_reservation.api.payment.domain.dto;

import com.server.concert_reservation.api.payment.domain.model.Payment;

import java.time.LocalDateTime;

public record PaymentInfo(Long id,
                          Long userId,
                          Long reservationId,
                          int amount,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {

    public static PaymentInfo of(Payment payment) {
        return new PaymentInfo(payment.getId(),
                payment.getUserId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}
