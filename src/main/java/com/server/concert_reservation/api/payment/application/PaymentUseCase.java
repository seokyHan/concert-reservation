package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.payment.domain.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.domain.dto.command.PaymentCommand;
import org.springframework.stereotype.Component;

public interface PaymentUseCase {

    PaymentInfo paymentReservation(PaymentCommand command);
}
