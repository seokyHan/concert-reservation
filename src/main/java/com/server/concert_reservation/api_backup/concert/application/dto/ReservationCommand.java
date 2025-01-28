package com.server.concert_reservation.api_backup.concert.application.dto;


import com.server.concert_reservation.interfaces.web.concert.dto.ConcertHttpRequest;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationCommand(Long userId,
                                 Long concertScheduleId,
                                 List<Long> seatIds,
                                 LocalDateTime dateTime) {

    public static ReservationCommand of(ConcertHttpRequest.ConcertReservationRequest request) {
        return new ReservationCommand(request.userId(), request.concertScheduleId(), request.seatIds(), request.dateTime());
    }
}
