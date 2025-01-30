package com.server.concert_reservation.domain.concert.dto;

import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;

import java.util.List;

public record ConcertSeatInfo(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeat) {

    public static ConcertSeatInfo of(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeat) {
        return new ConcertSeatInfo(concertSchedule, concertSeat);
    }

}
