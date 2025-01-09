package com.server.concert_reservation.api.payment.infrastructure.entity;

import com.server.concert_reservation.api.payment.domain.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentEntityTest {

    @DisplayName("Payment 엔티티를 Payment 도메인 모델로 변환한다.")
    @Test
    void paymentEntityCovertToPaymentDomainTest() {
        //given
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(1L)
                .userId(1L)
                .reservationId(2L)
                .amount(30000)
                .build();

        //when
        Payment payment = paymentEntity.toDomain();

        //then
        assertEquals(paymentEntity.getId(), payment.getId());
        assertEquals(paymentEntity.getUserId(), payment.getUserId());
        assertEquals(paymentEntity.getReservationId(), payment.getReservationId());
        assertEquals(paymentEntity.getAmount(), payment.getAmount());

    }

}