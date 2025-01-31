package com.server.concert_reservation.interfaces.web.payment.dto;

import com.server.concert_reservation.application.payment.dto.PaymentResult;

import java.time.LocalDateTime;

public class PaymentHttpResponse {

    public record PaymentResponse(Long id,
                                  Long userId,
                                  Long reservationId,
                                  int amount,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt
    ) {
        public static PaymentResponse of(PaymentResult payment) {
            return new PaymentResponse(payment.id(),
                    payment.userId(),
                    payment.reservationId(),
                    payment.amount(),
                    payment.createdAt(),
                    payment.updatedAt());
        }
    }
}
