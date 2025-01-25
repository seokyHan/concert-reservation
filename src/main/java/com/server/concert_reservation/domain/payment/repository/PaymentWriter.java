package com.server.concert_reservation.domain.payment.repository;

import com.server.concert_reservation.domain.payment.model.Payment;

public interface PaymentWriter {
    Payment save(Payment payment);
}
