package com.server.concert_reservation.api.concert.application.dto;

import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;

import java.util.List;

public record ConcertSeatInfo(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeatList) {

    public static ConcertSeatInfo of(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeatList) {
        return new ConcertSeatInfo(concertSchedule, concertSeatList);
    }
}
