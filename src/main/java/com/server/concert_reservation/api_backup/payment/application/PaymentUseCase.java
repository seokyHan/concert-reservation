package com.server.concert_reservation.api_backup.payment.application;

import com.server.concert_reservation.api_backup.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.api_backup.payment.application.dto.PaymentCommand;

public interface PaymentUseCase {

    PaymentInfo paymentReservation(PaymentCommand command);
}
