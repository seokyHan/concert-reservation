package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.payment.domain.model.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.domain.model.dto.PaymentCommand;

public interface PaymentUseCase {

    PaymentInfo paymentReservation(PaymentCommand command);
}
