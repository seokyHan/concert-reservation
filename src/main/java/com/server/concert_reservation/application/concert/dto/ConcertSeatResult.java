package com.server.concert_reservation.application.concert.dto;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;

import java.util.List;

public record ConcertSeatResult(ConcertScheduleInfo concertSchedule, List<ConcertSeatInfo> concertSeat) {

    public static ConcertSeatResult of(ConcertScheduleInfo concertSchedule, List<ConcertSeatInfo> concertSeat) {
        return new ConcertSeatResult(concertSchedule, concertSeat);
    }
}
