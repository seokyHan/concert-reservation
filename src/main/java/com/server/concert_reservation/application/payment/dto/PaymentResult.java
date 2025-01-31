package com.server.concert_reservation.application.payment.dto;

import com.server.concert_reservation.domain.payment.dto.PaymentInfo;

import java.time.LocalDateTime;

public record PaymentResult(Long id,
                            Long userId,
                            Long reservationId,
                            int amount,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt
) {

    public static PaymentResult from(PaymentInfo payment) {
        return new PaymentResult(payment.id(),
                payment.userId(),
                payment.reservationId(),
                payment.amount(),
                payment.createdAt(),
                payment.updatedAt());
    }
}
