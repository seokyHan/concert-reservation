package com.server.concert_reservation.domain.payment.service;

import com.server.concert_reservation.domain.payment.dto.PaymentInfo;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("임시 예약된 콘서트를 결제한다.")
    @Test
    void paymentReservationTest() {
        // given
        Long userId = 33L;
        Long reservationId = 11L;
        int totalPrice = 10000;

        // when
        PaymentInfo paymentInfo = paymentService.paymentReservation(userId, reservationId, totalPrice);

        // then
        assertAll(
                () -> assertEquals(userId, paymentInfo.userId()),
                () -> assertEquals(reservationId, paymentInfo.reservationId()),
                () -> assertEquals(totalPrice, paymentInfo.amount())
        );
    }

}