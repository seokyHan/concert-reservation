package com.server.concert_reservation.interfaces.web.payment.dto;

public class PaymentHttpRequest {

    public record PaymentRequest(Long userId,
                                 Long reservationId,
                                 String token
    ) {
    }
}
