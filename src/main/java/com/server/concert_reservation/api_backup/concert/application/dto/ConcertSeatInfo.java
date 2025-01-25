package com.server.concert_reservation.api_backup.concert.application.dto;

import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;

import java.util.List;

public record ConcertSeatInfo(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeatList) {

    public static ConcertSeatInfo of(ConcertSchedule concertSchedule, List<ConcertSeat> concertSeatList) {
        return new ConcertSeatInfo(concertSchedule, concertSeatList);
    }
}
