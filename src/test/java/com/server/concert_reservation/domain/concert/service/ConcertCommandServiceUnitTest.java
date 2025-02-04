package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.*;
import static com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus.*;
import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertCommandServiceUnitTest {

    @Mock
    private ConcertReader concertReader;

    @Mock
    private ConcertWriter concertWriter;

    @Mock
    private TimeManager timeManager;

    @InjectMocks
    private ConcertCommandService concertCommandService;


    @DisplayName("좌석 임시 예약에 성공한다.")
    @Test
    void successReserveSeats() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), AVAILABLE)
                .create();

        when(concertReader.getConcertSeatById(concertSeat.getId())).thenReturn(concertSeat);
        when(concertWriter.saveAll(anyList())).thenReturn(List.of(concertSeat));

        // when
        List<ConcertSeatInfo> result = concertCommandService.reserveSeats(List.of(concertSeat.getId()));

        // then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(concertSeat.getId(), result.get(0).id()),
                () -> assertEquals(concertSeat.getStatus(), result.get(0).status()),
                () -> assertEquals(concertSeat.getPrice(), result.get(0).price()),
                () -> assertEquals(concertSeat.getNumber(), result.get(0).number())
        );
    }

    @DisplayName("좌석 임시 예약에 실패한다. - 좌석의 상태가 AVAILABLE이 아닌 경우")
    @Test
    void failureReserveSeatsWhenStatusNotAvailable() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();

        when(concertReader.getConcertSeatById(concertSeat.getId())).thenReturn(concertSeat);

        //when //then
        assertThatThrownBy(() -> concertCommandService.reserveSeats(List.of(concertSeat.getId())))
                .isInstanceOf(CustomException.class)
                .hasMessage(CAN_NOT_RESERVE_SEAT.getMessage());
    }

    @DisplayName("예약 생성을 성공한다.")
    @Test
    void successCreateReservation() {
        // given
        Long userId = 1L;
        Long concertScheduleId = 1L;
        List<ConcertSeatInfo> seatIds = List.of(Instancio.of(ConcertSeatInfo.class)
                .set(field(ConcertSeatInfo::id), 1L)
                .create());

        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getUserId), userId)
                .set(field(Reservation::getConcertScheduleId), concertScheduleId)
                .set(field(Reservation::getSeatIds), List.of(seatIds.get(0).id()))
                .set(field(Reservation::getReservationAt), timeManager.now())
                .create();
        when(concertWriter.saveReservation(any(Reservation.class))).thenReturn(reservation);

        // when
        ReservationInfo reservationInfo = concertCommandService.createReservation(userId, concertScheduleId, seatIds);

        // then
        assertAll(
                () -> assertEquals(reservationInfo.id(), reservation.getId()),
                () -> assertEquals(reservationInfo.userId(), reservation.getUserId()),
                () -> assertEquals(reservationInfo.seatIds().get(0), reservation.getSeatIds().get(0)),
                () -> assertEquals(reservationInfo.concertScheduleId(), reservation.getConcertScheduleId())
        );

    }

    @DisplayName("예약 확정에 성공한다.")
    @Test
    void successCompleteTemporaryReserveConcert() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), RESERVING)
                .create();
        ConcertSeat concertSeat1 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        ConcertSeat concertSeat2 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();

        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);
        when(concertReader.getConcertSeatsByIds(anyList())).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        concertCommandService.completeReservation(reservation.getId());

        // then
        assertAll(
                () -> assertEquals(RESERVED, reservation.getStatus()),
                () -> assertEquals(SOLD, concertSeat1.getStatus()),
                () -> assertEquals(SOLD, concertSeat2.getStatus()),
                () -> then(concertWriter).should(times(1)).saveReservation(reservation),
                () -> then(concertWriter).should(times(1)).saveAll(List.of(concertSeat1, concertSeat2))
        );
    }

    @DisplayName("임시 예약 상태가 아니라면 예약 확정에 실패한다.")
    @Test
    void reservationCompleteFailWhenNotReserving() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), RESERVED)
                .create();

        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);

        // when & then
        assertThatThrownBy(() -> concertCommandService.completeReservation(reservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

    @DisplayName("임시 예약을 취소한다")
    @Test
    void cancelTemporaryReservation() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), RESERVING)
                .create();
        ConcertSeat concertSeat1 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        ConcertSeat concertSeat2 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();

        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);
        when(concertReader.getConcertSeatsByIds(anyList())).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        concertCommandService.cancelTemporaryReservation(reservation.getId());

        // when & then
        assertAll(
                () -> assertEquals(CANCELED, reservation.getStatus()),
                () -> assertEquals(AVAILABLE, concertSeat1.getStatus()),
                () -> assertEquals(AVAILABLE, concertSeat2.getStatus()),
                () -> then(concertWriter).should(times(1)).saveReservation(reservation),
                () -> then(concertWriter).should(times(1)).saveAll(List.of(concertSeat1, concertSeat2)));
    }


    @DisplayName("임시 예약 상태가 아니라면 예약을 취소할 수 없다.")
    @Test
    void whenNotTemporaryReservedCancelReservation() {
        // given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), CANCELED)
                .create();

        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);

        // when & then
        assertThatThrownBy(() -> concertCommandService.cancelTemporaryReservation(reservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(CANCEL_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }


}