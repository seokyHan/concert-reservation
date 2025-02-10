package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode.IS_NOT_TEMPORARY_RESERVATION;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus.RESERVED;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus.RESERVING;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertQueryServiceUnitTest {

    @Mock
    private ConcertReader concertReader;

    @InjectMocks
    private ConcertQueryService concertQueryService;

    @DisplayName("콘서트 예약 가능 시간이 아니라면 빈값을 반환하다.")
    @Test
    void noAvailableScheduleWhenNotInReservationTime() {
        // given
        Long concertScheduleId = 1L;
        LocalDateTime dateTime = LocalDateTime.now();
        when(concertReader.getConcertScheduleByConcertId(concertScheduleId)).thenReturn(List.of());

        // when
        List<ConcertScheduleInfo> result = concertQueryService.findAvailableConcertSchedules(1L, dateTime);

        // then
        assertEquals(0, result.size());
    }

    @DisplayName("콘서트 예약 가능 기간내에 조회시 예약 가능한 콘서트를 반환한다.")
    @Test
    void availableReservationScheduleWhenAvailableTime() {
        // given
        Long concertScheduleId = 1L;
        LocalDateTime dateTime = LocalDateTime.now();
        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .set(field(ConcertSchedule::getReservationStartAt), dateTime.minusDays(1L))
                .create();
        when(concertReader.getConcertScheduleByConcertId(concertScheduleId)).thenReturn(List.of(concertSchedule));

        // when
        List<ConcertScheduleInfo> result = concertQueryService.findAvailableConcertSchedules(1L, dateTime);

        // then
        assertEquals(1, result.size());
    }

    @DisplayName("예약 가능한 콘서트 좌석 조회 테스트. Status AVAILABLE 경우만 조회")
    @Test
    void getAvailableConcertSeatsTest() {
        // given
        ConcertSchedule concertSchedule = Instancio.create(ConcertSchedule.class);

        ConcertSeat concertSeat1 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), AVAILABLE)
                .create();
        ConcertSeat concertSeat2 = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getStatus), TEMPORARY_RESERVED)
                .create();
        when(concertReader.getConcertSeatByScheduleId(concertSchedule.getId())).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        List<ConcertSeatInfo> result = concertQueryService.findAvailableConcertSeats(concertSchedule.getId());

        // then
        assertEquals(1, result.size());
        assertEquals(AVAILABLE, result.get(0).status());
    }

    @DisplayName("콘서트 스케쥴 id로 콘서트 스케쥴을 조회한다.")
    @Test
    void getConcertScheduleById() {
        //given
        ConcertSchedule concertSchedule = Instancio.create(ConcertSchedule.class);
        when(concertReader.getConcertScheduleById(concertSchedule.getId())).thenReturn(concertSchedule);

        //when
        ConcertScheduleInfo concertScheduleInfo = concertQueryService.findConcertSchedule(concertSchedule.getId());

        //then
        assertNotNull(concertScheduleInfo);
        assertEquals(concertSchedule.getId(), concertScheduleInfo.id());
        assertEquals(concertSchedule.getConcertId(), concertScheduleInfo.concertId());
        assertEquals(concertSchedule.getRemainTicket(), concertScheduleInfo.remainTicket());
        assertEquals(concertSchedule.getReservationStartAt(), concertScheduleInfo.reservationStartAt());
    }

    @DisplayName("임시 예약이 만료된 예약 목록을 조회한다.")
    @Test
    void getTemporaryReservationByExpired() {
        //given
        Reservation reservation = Instancio.create(Reservation.class);
        when(concertReader.getTemporaryReservationsExpired(5)).thenReturn(List.of(reservation));

        //when
        List<ReservationInfo> reservationInfos = concertQueryService.findTemporaryReservationByExpired(5);

        //then
        assertEquals(reservationInfos.size(), 1);
        assertEquals(reservationInfos.get(0).id(), reservation.getId());
        assertEquals(reservationInfos.get(0).status(), reservation.getStatus());
        assertEquals(reservationInfos.get(0).userId(), reservation.getUserId());
        assertEquals(reservationInfos.get(0).seatIds(), reservation.getSeatIds());
    }

    @DisplayName("status가 RESERVING(예약중)이 아니라면 조회시 예외가 발생한다. ")
    @Test
    void throwsExceptionWhenStatusIsNotReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), RESERVED)
                .create();
        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);

        //when //then
        assertThatThrownBy(() -> concertQueryService.findTemporaryReservation(reservation.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(IS_NOT_TEMPORARY_RESERVATION.getMessage());
    }

    @DisplayName("예약중인 상태의 예약 내역을 조회한다.")
    @Test
    void getTemporaryReservationWhenStatusReserving() {
        //given
        Reservation reservation = Instancio.of(Reservation.class)
                .set(field(Reservation::getStatus), RESERVING)
                .create();
        when(concertReader.getReservationById(reservation.getId())).thenReturn(reservation);

        //when
        ReservationInfo reservationInfo = concertQueryService.findTemporaryReservation(reservation.getId());

        //then
        assertEquals(reservationInfo.id(), reservation.getId());
        assertEquals(reservationInfo.status(), reservation.getStatus());
        assertEquals(reservationInfo.userId(), reservation.getUserId());
        assertEquals(reservationInfo.seatIds(), reservation.getSeatIds());
    }


}