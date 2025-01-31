package com.server.concert_reservation.application.payment.dto;

import com.server.concert_reservation.interfaces.web.payment.dto.PaymentHttpRequest;


public record PaymentCommand(Long userId, Long reservationId, String token) {

    public static PaymentCommand from(PaymentHttpRequest.PaymentRequest request, String token) {
        return new PaymentCommand(request.userId(), request.reservationId(), token);
    }
}
