package com.server.concert_reservation.interfaces.web.concert.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttpRequest {

    public record ConcertReservationRequest(Long userId,
                                            Long concertScheduleId,
                                            List<Long> seatIds,
                                            LocalDateTime dateTime
    ) {
    }
}
