package com.server.concert_reservation.domain.payment.service;

import com.server.concert_reservation.domain.payment.dto.PaymentInfo;
import com.server.concert_reservation.domain.payment.model.Payment;
import com.server.concert_reservation.domain.payment.repository.PaymentWriter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentWriter paymentWriter;

    public PaymentInfo paymentReservation(Long userId, Long reservationId, int totalPrice) {
        val payment = paymentWriter.save(Payment.create(userId, reservationId, totalPrice));

        return PaymentInfo.from(payment);
    }
}
