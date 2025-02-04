package com.server.concert_reservation.domain.payment.service;

import com.server.concert_reservation.domain.payment.dto.PaymentInfo;
import com.server.concert_reservation.domain.payment.model.Payment;
import com.server.concert_reservation.domain.payment.repository.PaymentWriter;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentWriter paymentWriter;

    @DisplayName("결제 테스트")
    @Test
    void paymentReservationTest() {
        // given
        Long userId = 1L;
        Long reservationId = 1L;
        int totalPrice = 30000;

        Payment payment = Instancio.of(Payment.class)
                .set(field(Payment::getUserId), userId)
                .set(field(Payment::getReservationId), reservationId)
                .set(field(Payment::getAmount), totalPrice)
                .create();
        when(paymentWriter.save(any(Payment.class))).thenReturn(payment);

        // when
        PaymentInfo paymentInfo = paymentService.paymentReservation(userId, reservationId, totalPrice);

        // then
        assertEquals(paymentInfo.userId(), payment.getUserId());
        assertEquals(paymentInfo.reservationId(), payment.getReservationId());
        assertEquals(paymentInfo.amount(), payment.getAmount());
    }

}