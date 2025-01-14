package com.server.concert_reservation.api.concert.domain.model;

import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConcertScheduleTest {

    @DisplayName("예약 불가능한 날짜인 경우 예외가 발생한다")
    @Test
    void isAvailableReservePeriodTest() {
        //given
        LocalDateTime now = LocalDateTime.now();
        ConcertSchedule concertSchedule = ConcertSchedule.of(1L, 2L, 10, now.plusDays(1L), now.plusDays(2L), now, null);

        //when //then
        assertThrows(CustomException.class, () -> concertSchedule.isAvailableReservePeriod(now));
    }


}