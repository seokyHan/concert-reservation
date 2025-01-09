package com.server.concert_reservation.api.payment.domain.model;

import com.server.concert_reservation.api.payment.domain.dto.command.PaymentCommand;
import com.server.concert_reservation.api.payment.infrastructure.entity.PaymentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("PaymentCommand로 Payment 객체 생성 테스트")
    void createPaymentFromCommandTest() {
        // given
        PaymentCommand command = new PaymentCommand(1L, 1L, "test-test"); // userId와 reservationId를 포함하는 PaymentCommand 객체 생성
        int totalPrice = 10000;

        // when
        Payment payment = Payment.create(command, totalPrice);

        // then
        assertNotNull(payment);
        assertEquals(command.userId(), payment.getUserId());
        assertEquals(command.reservationId(), payment.getReservationId());
        assertEquals(totalPrice, payment.getAmount());
        assertNull(payment.getId());
    }

    @Test
    @DisplayName("PaymentEntity 변환 테스트")
    void toEntityTest() {
        // given
        Payment payment = createPayment(1L, 1L, 1L, 10000, LocalDateTime.now(), LocalDateTime.now());

        // when
        PaymentEntity paymentEntity = payment.toEntity(payment);

        // then
        assertNotNull(paymentEntity);
        assertEquals(payment.getId(), paymentEntity.getId());
        assertEquals(payment.getUserId(), paymentEntity.getUserId());
        assertEquals(payment.getReservationId(), paymentEntity.getReservationId());
        assertEquals(payment.getAmount(), paymentEntity.getAmount());
    }

    private Payment createPayment(Long id, Long userId, Long reservationId, int amount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Payment.of(id, userId, reservationId, amount, createdAt, updatedAt);
    }

}