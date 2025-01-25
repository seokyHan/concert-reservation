package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api_backup.concert.application.ConcertCommandService;
import com.server.concert_reservation.api_backup.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.api_backup.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus.CANCELED;
import static com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus.RESERVED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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


    @DisplayName("좌석 임시 예약 성공")
    @Test
    void temporaryReserveConcertTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(1L, 2L), now);

        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        when(concertReader.getConcertScheduleById(1L)).thenReturn(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .id(1L)
                .price(10000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .id(2L)
                .price(20000)
                .status(AVAILABLE)
                .build();

        when(concertReader.getConcertSeatById(1L)).thenReturn(concertSeat1);
        when(concertReader.getConcertSeatById(2L)).thenReturn(concertSeat2);

        Reservation concertReservation = Reservation.createReservation(command, 30000, timeManager.now());
        when(concertWriter.saveAll(anyList())).thenReturn(List.of(concertSeat1, concertSeat2));
        when(concertWriter.saveReservation(any(Reservation.class))).thenReturn(concertReservation);

        // when
        ReservationInfo result = concertCommandService.temporaryReserveConcert(command);

        // then
        assertAll(
                () -> assertEquals(30000, result.totalPrice()),
                () -> assertEquals(2, result.seatIds().size()),
                () -> assertEquals(ReservationStatus.RESERVING, result.status())
        );
    }

    @DisplayName("콘서트 예약이 불가능한 기간이면 예외가 발생한다.")
    @Test
    void thrownExceptionForUnavailableReservationPeriodTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(1L, 2L), now);

        ConcertSchedule concertSchedule = ConcertSchedule.of(1L, 2L, 10, now.plusDays(1L), now.plusDays(2L), now, null);
        when(concertReader.getConcertScheduleById(1L)).thenReturn(concertSchedule);

        // when & then
        assertThatThrownBy(() -> concertCommandService.temporaryReserveConcert(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(CAN_NOT_RESERVE_DATE.getMessage());
    }

    @DisplayName("콘서트 좌석이 예약 불가능하면 예외가 발생한다.")
    @Test
    void thrownExceptionForUnavailableSeatPeriodTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ConcertSchedule concertSchedule = ConcertSchedule.of(1L, 1L, 10, now.minusDays(1L), now.plusDays(1), now, null);
        when(concertReader.getConcertScheduleById(1L)).thenReturn(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .id(1L)
                .price(10000)
                .status(TEMPORARY_RESERVED)
                .build();
        when(concertReader.getConcertSeatById(1L)).thenReturn(concertSeat1);

        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(1L), now);
        // when & then
        assertThatThrownBy(() -> concertCommandService.temporaryReserveConcert(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(CAN_NOT_RESERVE_SEAT.getMessage());
    }

    @DisplayName("예약 확정에 성공한다.")
    @Test
    void successTemporaryReserveConcertTest() {
        // given
        Long reservationId = 1L;
        Reservation concertReservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.RESERVING)
                .seatIds(List.of(1L, 2L))
                .build();

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .id(1L)
                .price(10000)
                .status(TEMPORARY_RESERVED)
                .build();
        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .id(2L)
                .price(20000)
                .status(TEMPORARY_RESERVED)
                .build();

        when(concertReader.getReservationById(reservationId)).thenReturn(concertReservation);
        when(concertReader.getConcertSeatsByIds(anyList())).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        concertCommandService.completeReservation(reservationId);

        // then
        assertAll(
                () -> assertEquals(RESERVED, concertReservation.getStatus()),
                () -> assertEquals(SOLD, concertSeat1.getStatus()),
                () -> assertEquals(SOLD, concertSeat2.getStatus()),
                () -> then(concertWriter).should(times(1)).saveReservation(concertReservation),
                () -> then(concertWriter).should(times(1)).saveAll(List.of(concertSeat1, concertSeat2))
        );
    }

    @DisplayName("임시 예약 상태가 아니라면 예약 확정에 실패한다.")
    @Test
    void reservationCompleteFailWhenNotReservingTest() {
        // given
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.RESERVED)
                .seatIds(List.of(1L, 2L))
                .build();

        when(concertReader.getReservationById(reservationId)).thenReturn(reservation);

        // when & then
        assertThatThrownBy(() -> concertCommandService.completeReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .hasMessage(PAYMENT_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }

    @DisplayName("임시 예약을 취소한다")
    @Test
    void cancelTemporaryReservationTest() {
        // given
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.RESERVING)
                .seatIds(List.of(1L, 2L))
                .build();

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .id(1L)
                .price(10000)
                .status(TEMPORARY_RESERVED)
                .build();
        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .id(2L)
                .price(20000)
                .status(TEMPORARY_RESERVED)
                .build();

        when(concertReader.getReservationById(reservationId)).thenReturn(reservation);
        when(concertReader.getConcertSeatsByIds(anyList())).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        concertCommandService.cancelTemporaryReservation(reservationId);

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
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.CANCELED)
                .seatIds(List.of(1L, 2L))
                .build();

        when(concertReader.getReservationById(reservationId)).thenReturn(reservation);

        // when & then
        assertThatThrownBy(() -> concertCommandService.cancelTemporaryReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .hasMessage(CANCEL_ONLY_FOR_TEMP_RESERVATION.getMessage());
    }


}