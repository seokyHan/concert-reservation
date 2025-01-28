package com.server.concert_reservation.interfaces.web.payment.dto;

import com.server.concert_reservation.api_backup.payment.application.dto.PaymentInfo;

import java.time.LocalDateTime;

public class PaymentHttpResponse {

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
