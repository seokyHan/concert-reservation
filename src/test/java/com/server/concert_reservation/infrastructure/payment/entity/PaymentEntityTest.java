package com.server.concert_reservation.infrastructure.payment.entity;

import com.server.concert_reservation.domain.payment.model.Payment;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentEntityTest {

    @DisplayName("Payment 엔티티를 Payment 도메인 모델로 변환한다.")
    @Test
    void paymentEntityCovertToPaymentDomain() {
        //given
        PaymentEntity paymentEntity = Instancio.create(PaymentEntity.class);

        //when
        Payment payment = paymentEntity.toDomain();

        //then
        assertEquals(paymentEntity.getId(), payment.getId());
        assertEquals(paymentEntity.getUserId(), payment.getUserId());
        assertEquals(paymentEntity.getReservationId(), payment.getReservationId());
        assertEquals(paymentEntity.getAmount(), payment.getAmount());

    }

}