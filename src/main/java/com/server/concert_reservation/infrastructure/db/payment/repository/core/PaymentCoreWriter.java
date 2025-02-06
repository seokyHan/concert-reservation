package com.server.concert_reservation.infrastructure.db.payment.repository.core;

import com.server.concert_reservation.domain.payment.model.Payment;
import com.server.concert_reservation.domain.payment.repository.PaymentWriter;
import com.server.concert_reservation.infrastructure.db.payment.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentCoreWriter implements PaymentWriter {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment.toEntity(payment)).toDomain();
    }
}
