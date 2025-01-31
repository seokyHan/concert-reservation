package com.server.concert_reservation.domain.payment.dto;

import com.server.concert_reservation.domain.payment.model.Payment;

import java.time.LocalDateTime;

public record PaymentInfo(Long id,
                          Long userId,
                          Long reservationId,
                          int amount,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {

    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(payment.getId(),
                payment.getUserId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}
