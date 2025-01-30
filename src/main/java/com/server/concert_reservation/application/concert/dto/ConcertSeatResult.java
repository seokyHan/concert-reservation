package com.server.concert_reservation.application.concert.dto;

import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;

public record ConcertSeatResult(ConcertSeatInfo concertSeatInfo) {

    public static ConcertSeatResult of(ConcertSeatInfo concertSeatInfo) {
        return new ConcertSeatResult(concertSeatInfo);
    }
}
