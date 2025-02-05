package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.Concert;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.IS_NOT_TEMPORARY_RESERVATION;
import static com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus.RESERVED;
import static com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus.RESERVING;
import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class ConcertQueryServiceIntegrationTest {

    @Autowired
    private ConcertQueryService concertQueryService;
    @Autowired
    private ConcertWriter concertWriter;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("예약 가능한 콘서트 스케쥴 조회")
    @Test
    void getAvailableConcertSchedulesTest() {
        LocalDateTime now = LocalDateTime.now();
        // given
        Concert concert = Instancio.of(Concert.class)
                .ignore(field(Concert::getId))
                .create();
        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .ignore(field(ConcertSchedule::getId))
                .set(field(ConcertSchedule::getConcertId), savedConcert.getId())
                .set(field(ConcertSchedule::getReservationStartAt), now.minusDays(1L))
                .set(field(ConcertSchedule::getRemainTicket), 10)
                .create();
        concertWriter.save(concertSchedule);

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = concertQueryService.findAvailableConcertSchedules(savedConcert.getId(), now);

        // then
        assertAll(
                () -> assertEquals(1, availableConcertSchedule.size()),
                () -> assertEquals(savedConcert.getId(), availableConcertSchedule.get(0).concertId())
        );
    }

    @DisplayName("예약 가능한 콘서트 좌석 조회 테스트.")
    @Test
    void getAvailableConcertSeatsTest() {
        // given
        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .ignore(field(ConcertSchedule::getId))
                .create();
        concertWriter.save(concertSchedule);

        ConcertSeat concertSeat1 = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getConcertScheduleId), concertSchedule.getId())
                .set(field(ConcertSeat::getStatus), AVAILABLE)
                .create();
        ConcertSeat concertSeat2 = Instancio.of(ConcertSeat.class)
                .ignore(field(ConcertSeat::getId))
                .set(field(ConcertSeat::getConcertScheduleId), concertSchedule.getId())
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        concertWriter.saveAll(List.of(concertSeat1, concertSeat2));


        // when
        List<ConcertSeatInfo> result = concertQueryService.findAvailableConcertSeats(concertSchedule.getId());

        // then
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(concertSchedule.getId(), result.get(0).concertScheduleId()),
                () -> assertEquals(AVAILABLE, result.get(0).status())
        );
    }

    @DisplayName("예약 가능 시기가 아닌 날짜로 조회하면 빈리스트가 반환된다.")
    @Test
    void notAvailableConcertSchedulesTobeEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        // given
        Concert concert = Instancio.of(Concert.class)
                .ignore(field(Concert::getId))
                .create();
        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .ignore(field(ConcertSchedule::getId))
                .set(field(ConcertSchedule::getConcertId), concert.getId())
                .set(field(ConcertSchedule::getReservationStartAt), now.plusDays(1L))
                .create();
        concertWriter.save(concertSchedule);

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = concertQueryService.findAvailableConcertSchedules(savedConcert.getId(), now);

        // then
        assertAll(
                () -> assertEquals(0, availableConcertSchedule.size())
        );
    }

    @DisplayName("콘서트 스케쥴 id로 콘서트 스케쥴을 조회한다.")
    @Test
    void getConcertScheduleById() {
        //given
        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .ignore(field(ConcertSchedule::getId))
                .create();
        ConcertSchedule savedConcertSchedule = concertWriter.save(concertSchedule);

        //when
        ConcertScheduleInfo concertScheduleInfo = concertQueryService.findConcertSchedule(savedConcertSchedule.getId());

        //then
        assertAll(
                () -> assertNotNull(concertScheduleInfo),
                () -> assertEquals(savedConcertSchedule.getId(), concertScheduleInfo.id()),
                () -> assertEquals(savedConcertSchedule.getConcertId(), concertScheduleInfo.concertId()),
                () -> assertEquals(savedConcertSchedule.getRemainTicket(), concertScheduleInfo.remainTicket()),
                () -> assertEquals(savedConcertSchedule.getReservationStartAt(), concertScheduleInfo.reservationStartAt())
        );
    }

    @DisplayName("임시예약 만료 토큰 목록 조회 테스트 - 스케쥴러")
    @Test
    void getTemporaryReservationByExpiredTest() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .set(field(Reservation::getReservationAt), LocalDateTime.now().minusMinutes(11))
                .create();
        concertWriter.saveReservation(reservation);

        //when
        List<ReservationInfo> temporaryReservationByExpired = concertQueryService.findTemporaryReservationByExpired(10);

        //then
        assertAll(
                () -> assertEquals(1, temporaryReservationByExpired.size()),
                () -> assertEquals(RESERVING, temporaryReservationByExpired.get(0).status())
        );
    }

    @DisplayName("status가 RESERVING(예약중)이 아니라면 조회시 예외가 발생한다. ")
    @Test
    void throwsExceptionWhenStatusIsNotReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVED)
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        //when //then
        assertThatThrownBy(() -> concertQueryService.findTemporaryReservation(saveReservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(IS_NOT_TEMPORARY_RESERVATION.getMessage());
    }

    @DisplayName("예약중인 상태의 예약 내역을 조회한다.")
    @Test
    void getTemporaryReservationWhenStatusReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .ignore(field(Reservation::getId))
                .set(field(Reservation::getStatus), RESERVING)
                .create();
        Reservation saveReservation = concertWriter.saveReservation(reservation);

        //when
        ReservationInfo reservationInfo = concertQueryService.findTemporaryReservation(saveReservation.getId());

        //then
        assertEquals(reservationInfo.id(), saveReservation.getId());
        assertEquals(reservationInfo.status(), saveReservation.getStatus());
        assertEquals(reservationInfo.userId(), saveReservation.getUserId());
        assertEquals(reservationInfo.seatIds(), saveReservation.getSeatIds());
    }


}

