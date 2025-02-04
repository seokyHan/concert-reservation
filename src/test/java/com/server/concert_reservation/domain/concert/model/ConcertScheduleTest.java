package com.server.concert_reservation.domain.concert.model;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcertScheduleTest {

    @DisplayName("예약 불가능한 날짜인 경우 예외가 발생한다")
    @Test
    void unavailableReservationDateThrowsException() {
        //given
        LocalDateTime now = LocalDateTime.now();
        ConcertSchedule concertSchedule = Instancio.of(ConcertSchedule.class)
                .set(field(ConcertSchedule::getReservationStartAt), now.minusDays(1L))
                .set(field(ConcertSchedule::getReservationEndAt), now.plusDays(1L))
                .create();

        //when //then
        assertEquals(true, concertSchedule.isAvailableReservePeriod(now));
    }


}