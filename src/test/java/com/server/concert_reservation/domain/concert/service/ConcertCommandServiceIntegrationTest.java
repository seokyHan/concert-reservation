package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.*;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus.*;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ConcertCommandServiceIntegrationTest {

    @Autowired
    private ConcertCommandService concertCommandService;
    @Autowired
    private ConcertWriter concertWriter;
    @Autowired
    private ConcertReader concertReader;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("콘서트 좌석 임시 예약에 성공한다.")
    @Test
    void temporaryReserveConcertSuccessTest() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), AVAILABLE)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        // when
        List<ConcertSeatInfo> result = concertCommandService.reserveSeats(List.of(concertSeats.get(0).getId()));

        // then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(concertSeats.get(0).getId(), result.get(0).id()),
                () -> assertEquals(TEMPORARY_RESERVED, result.get(0).status()),
                () -> assertEquals(concertSeats.get(0).getPrice(), result.get(0).price()),
                () -> assertEquals(concertSeats.get(0).getNumber(), result.get(0).number())
        );
    }

    @DisplayName("좌석 임시 예약에 실패한다. - 좌석의 상태가 AVAILABLE이 아닌 경우")
    @Test
    void failureReserveSeatsWhenStatusNotAvailable() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        //when //then
        assertThatThrownBy(() -> concertCommandService.reserveSeats(List.of(concertSeats.get(0).getId())))
                .isInstanceOf(CustomException.class)
                .hasMessage(CAN_NOT_RESERVE_SEAT.getMessage());
    }

    @DisplayName("예약 생성에 성공한다.")
    @Test
    void successCreateReservation() {
        // given
        Long userId = 1L;
        Long concertScheduleId = 1L;
        List<ConcertSeatInfo> seatIds = List.of(Instancio.of(ConcertSeatInfo.class)
                .set(field(ConcertSeatInfo::id), 1L)
                .create());

        // when
        ReservationInfo reservationInfo = concertCommandService.createReservation(userId, concertScheduleId, seatIds);

        // then
        assertAll(
                () -> assertEquals(reservationInfo.userId(), userId),
                () -> assertEquals(reservationInfo.seatIds().get(0), seatIds.get(0).id()),
                () -> assertEquals(reservationInfo.concertScheduleId(), concertScheduleId),
                () -> assertEquals(reservationInfo.status(), RESERVING)
        );

    }

    @DisplayName("임시 예약 확정에 성공한다. - 스케쥴러")
    @Test
    void successCompleteTemporaryReserveConcert() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .set(field(Reservation::getSeatIds), List.of(concertSeats.get(0).getId()))
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        // when
        concertCommandService.completeReservation(saveReservation.getId());
        Reservation getReservation = concertReader.getReservationById(saveReservation.getId());
        List<ConcertSeat> getConcertSeatsByIds = concertReader.getConcertSeatsByIds(List.of(concertSeats.get(0).getId()));

        // then
        assertAll(
                () -> assertEquals(RESERVED, getReservation.getStatus()),
                () -> assertEquals(SOLD, getConcertSeatsByIds.get(0).getStatus())
        );
    }

    @DisplayName("예약 상태가 RESERVING이 아닌 상태에서 예약 확정시 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenCompleteReservationStatusNotReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), CANCELED)
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);


        //when & then
        assertThatThrownBy(() -> concertCommandService.completeReservation(saveReservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

    @DisplayName("좌석 상태가 TEMPORARY_RESERVED가 아닌 상태에서 예약 확정시 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenCompleteConcertSeatStatusNotTemporaryReserved() {
        //given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), SOLD)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .set(field(Reservation::getSeatIds), List.of(concertSeats.get(0).getId()))
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        //when & then
        assertThatThrownBy(() -> concertCommandService.completeReservation(saveReservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(RESERVING_ONLY.getMessage());
    }

    @DisplayName("임시 예약 취소에 성공한다. - 스케쥴러")
    @Test
    void successCancelTemporaryReserveConcert() {
        // given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .set(field(Reservation::getSeatIds), List.of(concertSeats.get(0).getId()))
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        // when
        concertCommandService.cancelTemporaryReservation(saveReservation.getId());
        Reservation getReservation = concertReader.getReservationById(saveReservation.getId());
        List<ConcertSeat> getConcertSeatsByIds = concertReader.getConcertSeatsByIds(List.of(concertSeats.get(0).getId()));

        // then
        assertAll(
                () -> assertEquals(CANCELED, getReservation.getStatus()),
                () -> assertEquals(AVAILABLE, getConcertSeatsByIds.get(0).getStatus())
        );
    }

    @DisplayName("예약 상태가 RESERVING이 아닌 상태에서 예약 취소시 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenCancelReservationStatusNotReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), CANCELED)
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);


        //when & then
        assertThatThrownBy(() -> concertCommandService.cancelTemporaryReservation(saveReservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(CANCEL_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

    @DisplayName("좌석 상태가 SOLD 상태라면 예약 취소시 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenCancelConcertSeatStatusSold() {
        //given
        ConcertSeat concertSeat = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getStatus), SOLD)
                .create();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat));

        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .set(field(Reservation::getSeatIds), List.of(concertSeats.get(0).getId()))
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        //when & then
        assertThatThrownBy(() -> concertCommandService.cancelTemporaryReservation(saveReservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_SOLD_SEAT.getMessage());
    }


}