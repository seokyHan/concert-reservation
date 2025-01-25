package com.server.concert_reservation.api.concert.domain.model;

import com.server.concert_reservation.api_backup.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.CANCEL_ONLY_FOR_TEMP_RESERVATION;
import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.PAYMENT_ONLY_FOR_TEMP_RESERVATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReservationTest {

    @Mock
    private TimeManager timeManager;

    @Test
    @DisplayName("예약 생성 테스트")
    void createReservationTest() {
        // given
        int totalPrice = 20000;
        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(101L, 102L), LocalDateTime.now());

        // when
        Reservation reservation = Reservation.createReservation(command, totalPrice, LocalDateTime.now());

        // then
        assertNotNull(reservation);
        assertEquals(1L, reservation.getUserId());
        assertEquals(Arrays.asList(101L, 102L), reservation.getSeatIds());
        assertEquals(ReservationStatus.RESERVING, reservation.getStatus());
        assertEquals(totalPrice, reservation.getTotalPrice());
        assertNotNull(reservation.getReservationAt());
    }

    @Test
    @DisplayName("예약 완료 테스트")
    void completeReservationTest() {
        // given
        Reservation reservation = Reservation.createReservation(
                new ReservationCommand(1L, 1L, List.of(101L, 102L), LocalDateTime.now())
                , 1000,
                timeManager.now());

        // when
        reservation.complete();

        // then
        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
    }

    @Test
    @DisplayName("예약 완료 실패 테스트 - 상태가 RESERVING이 아닐 때")
    void completeReservationFailTest() {
        // given
        Reservation reservation = Reservation.of(
                1L,
                2L,
                List.of(1L, 3L),
                ReservationStatus.CANCELED,
                10000,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );


        // when & then
        assertThatThrownBy(() -> reservation.complete())
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_ONLY_FOR_TEMP_RESERVATION.getMessage());

    }

    @Test
    @DisplayName("임시 예약 취소 테스트")
    void cancelTemporaryReservationTest() {
        // given
        Reservation reservation = Reservation.createReservation(
                new ReservationCommand(1L, 1L, List.of(101L, 102L), LocalDateTime.now()),
                20000,
                timeManager.now());

        // when
        reservation.cancelTemporaryReservation();

        // then
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
    }

    @Test
    @DisplayName("임시 예약 취소 실패 테스트 - 상태가 RESERVING이 아닐 때")
    void cancelTemporaryReservationFailTest() {
        // given
        Reservation reservation = Reservation.of(
                1L,
                2L,
                List.of(1L, 3L),
                ReservationStatus.CANCELED,
                10000,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        //when & then
        assertThatThrownBy(() -> reservation.cancelTemporaryReservation())
                .isInstanceOf(CustomException.class)
                .hasMessage(CANCEL_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

}