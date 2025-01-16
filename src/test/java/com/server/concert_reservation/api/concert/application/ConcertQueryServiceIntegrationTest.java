package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.application.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus.RESERVING;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConcertQueryServiceIntegrationTest {

    @Autowired
    private ConcertQueryUseCase concertQueryUseCase;
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
        Concert concert = Concert.builder()
                .title("콘서트1")
                .description("test")
                .build();

        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.minusDays(1L))
                .reservationEndAt(now.plusDays(1L))
                .build();

        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = concertQueryUseCase.getAvailableConcertSchedules(saveConcertSchedule.getConcertId(), now);

        // then
        assertAll(
                () -> assertEquals(1, availableConcertSchedule.size()),
                () -> assertEquals(10, availableConcertSchedule.get(0).remainTicket())
        );
    }

    @DisplayName("예약 가능한 콘서트 좌석 조회 테스트.")
    @Test
    void getAvailableConcertSeatsTest() {
        // given
        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(1L)
                .reservationStartAt(LocalDateTime.now().minusDays(1L))
                .reservationEndAt(LocalDateTime.now().plusDays(1L))
                .build();
        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .concertScheduleId(saveConcertSchedule.getId())
                .price(10000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .concertScheduleId(saveConcertSchedule.getId())
                .price(20000)
                .status(TEMPORARY_RESERVED)
                .build();
        concertWriter.saveAll(List.of(concertSeat1, concertSeat2));


        // when
        ConcertSeatInfo result = concertQueryUseCase.getAvailableConcertSeats(1L);

        // then
        assertAll(
                () -> assertEquals(concertSchedule.getConcertId(), result.concertSchedule().getConcertId()),
                () -> assertEquals(1, result.concertSeatList().size()),
                () -> assertEquals(10000, result.concertSeatList().get(0).getPrice()),
                () -> assertEquals(saveConcertSchedule.getId(), result.concertSeatList().get(0).getConcertScheduleId()),
                () -> assertEquals(AVAILABLE, result.concertSeatList().get(0).getStatus())
        );
    }

    @DisplayName("예약 가능 시기가 아닌 날짜로 조회하면 빈리스트가 반환된다.")
    @Test
    void notAvailableConcertSchedulesTobeEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        // given
        Concert concert = Concert.builder()
                .title("콘서트1")
                .description("test")
                .build();

        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.minusDays(1L))
                .reservationEndAt(now.plusDays(1L))
                .build();

        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = concertQueryUseCase.getAvailableConcertSchedules(saveConcertSchedule.getConcertId(), now.plusDays(2L));

        // then
        assertAll(
                () -> assertEquals(0, availableConcertSchedule.size())
        );
    }

    @DisplayName("임시예약 만료 토큰 목록 조회 테스트 - 스케쥴러")
    @Test
    void getTemporaryReservationByExpiredTest() {
        //given
        LocalDateTime now = LocalDateTime.now();
        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(101L, 102L), LocalDateTime.now());
        Reservation reservation = Reservation.createReservation(command, 10000, now.minusMinutes(15));
        concertWriter.saveReservation(reservation);

        //when
        List<Reservation> temporaryReservationByExpired = concertQueryUseCase.getTemporaryReservationByExpired(10);

        //then
        assertAll(
                () -> assertEquals(1, temporaryReservationByExpired.size()),
                () -> assertEquals(10000, temporaryReservationByExpired.get(0).getTotalPrice()),
                () -> assertEquals(RESERVING, temporaryReservationByExpired.get(0).getStatus())
        );
    }



}