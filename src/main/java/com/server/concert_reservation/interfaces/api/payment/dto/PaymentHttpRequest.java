package com.server.concert_reservation.interfaces.api.payment.dto;

public class PaymentHttpRequest {

    public record PaymentRequest(Long userId,
                                 Long reservationId,
                                 String token
    ) {
    }
}
