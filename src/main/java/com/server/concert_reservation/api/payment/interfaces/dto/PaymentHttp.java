package com.server.concert_reservation.api.payment.interfaces.dto;

import com.server.concert_reservation.api.payment.application.dto.PaymentInfo;

import java.time.LocalDateTime;

public class PaymentHttp {
    public record PaymentRequest(Long userId,
                                 Long reservationId,
                                 String token) {}
    public record PaymentResponse(Long id,
                                  Long userId,
                                  Long reservationId,
                                  int amount,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt
    ) {
        public static PaymentResponse of(PaymentInfo paymentInfo) {
            return new PaymentResponse(paymentInfo.id(),
                    paymentInfo.userId(),
                    paymentInfo.reservationId(),
                    paymentInfo.amount(),
                    paymentInfo.createdAt(),
                    paymentInfo.updatedAt());
        }
    }
}
