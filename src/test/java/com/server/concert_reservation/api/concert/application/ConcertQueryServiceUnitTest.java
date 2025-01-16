package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.application.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
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
        when(concertReader.getConcertScheduleByConcertIdAndDate(concertScheduleId, dateTime)).thenReturn(List.of());

        // when
        List<ConcertScheduleInfo> result = concertQueryService.getAvailableConcertSchedules(1L, dateTime);

        // then
        assertEquals(0, result.size());
    }

    @DisplayName("콘서트 예약 가능 기간내에 조회시 예약 가능한 콘서트를 반환한다.")
    @Test
    void availableReservationScheduleWhenAvailableTime() {
        // given
        Long concertScheduleId = 1L;
        LocalDateTime dateTime = LocalDateTime.now();
        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .id(1L)
                .reservationStartAt(LocalDateTime.parse("2025-01-01T00:00:00"))
                .reservationEndAt(LocalDateTime.parse("2025-01-30T23:59:59"))
                .build();

        when(concertReader.getConcertScheduleByConcertIdAndDate(concertScheduleId, dateTime)).thenReturn(List.of(concertSchedule));

        // when
        List<ConcertScheduleInfo> result = concertQueryService.getAvailableConcertSchedules(1L, dateTime);

        // then
        assertEquals(1, result.size());
    }

    @DisplayName("예약 가능한 콘서트 좌석 조회 테스트.")
    @Test
    void getAvailableConcertSeatsTest() {
        // given
        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .id(1L)
                .concertId(1L)
                .reservationStartAt(LocalDateTime.parse("2024-09-20T00:00:00"))
                .reservationEndAt(LocalDateTime.parse("2024-09-20T00:00:00"))
                .build();

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .id(1L)
                .concertScheduleId(1L)
                .price(10000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .id(2L)
                .concertScheduleId(1L)
                .price(20000)
                .status(TEMPORARY_RESERVED)
                .build();

        when(concertReader.getConcertScheduleById(1L)).thenReturn(concertSchedule);
        when(concertReader.getConcertSeatByScheduleId(1L)).thenReturn(List.of(concertSeat1, concertSeat2));

        // when
        ConcertSeatInfo result = concertQueryService.getAvailableConcertSeats(1L);

        // then
        assertAll(
                () -> assertEquals(concertSchedule, result.concertSchedule()),
                () -> assertThat(result.concertSeatList()).containsExactly(concertSeat1),
                () -> assertEquals(1, result.concertSeatList().size())
        );
    }

}