package com.server.concert_reservation.domain.payment.model;

import com.server.concert_reservation.infrastructure.db.payment.entity.PaymentEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("Payment 객체 생성 테스트")
    void createPayment() {
        // given
        Long userId = 1L;
        Long reservationId = 1L;
        int totalPrice = 10000;

        // when
        Payment payment = Payment.create(userId, reservationId, totalPrice);

        // then
        assertNotNull(payment);
        assertEquals(userId, payment.getUserId());
        assertEquals(reservationId, payment.getReservationId());
        assertEquals(totalPrice, payment.getAmount());
        assertNull(payment.getId());
    }

    @Test
    @DisplayName("Payment 인스턴스 Entity 변환 테스트")
    void PaymentInstanceConvertToEntity() {
        // given
        Payment payment = Instancio.create(Payment.class);

        // when
        PaymentEntity paymentEntity = payment.toEntity(payment);

        // then
        assertNotNull(paymentEntity);
        assertEquals(payment.getId(), paymentEntity.getId());
        assertEquals(payment.getUserId(), paymentEntity.getUserId());
        assertEquals(payment.getReservationId(), paymentEntity.getReservationId());
        assertEquals(payment.getAmount(), paymentEntity.getAmount());
    }

}