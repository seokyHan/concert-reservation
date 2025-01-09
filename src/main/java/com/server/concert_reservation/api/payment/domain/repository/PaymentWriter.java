package com.server.concert_reservation.api.payment.domain.repository;

import com.server.concert_reservation.api.payment.domain.model.Payment;
import org.springframework.stereotype.Repository;

public interface PaymentWriter {
    Payment save(Payment payment);
}
