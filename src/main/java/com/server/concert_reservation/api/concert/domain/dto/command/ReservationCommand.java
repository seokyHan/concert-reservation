package com.server.concert_reservation.api.concert.domain.dto.command;

import com.server.concert_reservation.api.concert.presentation.dto.ConcertHttp;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationCommand(Long userId,
                                 Long concertScheduleId,
                                 List<Long> seatIds,
                                 LocalDateTime dateTime) {

    public static ReservationCommand of(ConcertHttp.ConcertReservationRequest request) {
        return new ReservationCommand(request.userId(), request.concertScheduleId(), request.seatIds(), request.dateTime());
    }
}
