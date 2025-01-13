package com.server.concert_reservation.api.payment.domain.model.dto;

import com.server.concert_reservation.api.payment.presentation.dto.PaymentHttp;


public record PaymentCommand(Long userId, Long reservationId, String token) {

    public static PaymentCommand from(PaymentHttp.PaymentRequest request, String token) {
        return new PaymentCommand(request.userId(), request.reservationId(), token);
    }
}
