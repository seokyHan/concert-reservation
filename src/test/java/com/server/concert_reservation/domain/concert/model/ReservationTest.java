package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.CANCEL_ONLY_FOR_TEMP_RESERVATION;
import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.PAYMENT_ONLY_FOR_TEMP_RESERVATION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReservationTest {

    @Test
    @DisplayName("예약 생성 테스트")
    void createReservation() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getUserId), 1L)
                .set(field(Reservation::getConcertScheduleId), 2L)
                .set(field(Reservation::getSeatIds), List.of(1L))
                .set(field(Reservation::getTotalPrice), 20000)
                .set(field(Reservation::getStatus), ReservationStatus.RESERVING)
                .create();

        // then
        assertNotNull(reservation);
        assertEquals(1L, reservation.getUserId());
        assertEquals(2L, reservation.getConcertScheduleId());
        assertEquals(1L, reservation.getSeatIds().get(0));
        assertEquals(20000, reservation.getTotalPrice());
        assertEquals(ReservationStatus.RESERVING, reservation.getStatus());
    }

    @Test
    @DisplayName("예약 완료 테스트")
    void completeReservation() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), ReservationStatus.RESERVING)
                .create();

        // when
        reservation.complete();

        // then
        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
    }

    @Test
    @DisplayName("예약 완료 실패 테스트 - 상태가 RESERVING이 아닐 때")
    void reservationFailTestWhenStatusNotReserving() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), ReservationStatus.CANCELED)
                .create();

        // when & then
        assertThatThrownBy(() -> reservation.complete())
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_ONLY_FOR_TEMP_RESERVATION.getMessage());

    }

    @Test
    @DisplayName("임시 예약 취소 테스트")
    void cancelTemporaryReservation() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), ReservationStatus.RESERVING)
                .create();

        // when
        reservation.cancelTemporaryReservation();

        // then
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
    }

    @Test
    @DisplayName("임시 예약 취소 실패 테스트 - 상태가 RESERVING이 아닐 때")
    void cancelTemporaryReservationFailTestWhenStatusNotReserving() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), ReservationStatus.RESERVED)
                .create();

        //when & then
        assertThatThrownBy(() -> reservation.cancelTemporaryReservation())
                .isInstanceOf(CustomException.class)
                .hasMessage(CANCEL_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

}