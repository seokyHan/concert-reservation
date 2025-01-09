package com.server.concert_reservation.api.concert.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConcertScheduleTest {

    @DisplayName("예약 가능 기간 내의 날짜가 true를 반환한다.")
    @Test
    void isAvailableReservePeriodTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        ConcertSchedule concertSchedule = ConcertSchedule.of(
                1L,
                1L,
                100,
                now.minusHours(1),
                now.plusHours(1),
                now,
                now
        );

        // when
        boolean result = concertSchedule.isAvailableReservePeriod(now);

        // then
        assertTrue(result);
    }


}