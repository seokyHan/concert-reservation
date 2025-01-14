package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;

public interface PaymentUseCase {

    PaymentInfo paymentReservation(PaymentCommand command);
}
