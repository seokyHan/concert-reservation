package com.server.concert_reservation.interfaces.web.concert.dto;

import com.server.concert_reservation.application.concert.dto.ConcertScheduleResult;
import com.server.concert_reservation.application.concert.dto.ConcertSeatResult;
import com.server.concert_reservation.application.concert.dto.ReservationResult;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttpResponse {

    public record ConcertScheduleResponse(List<ConcertScheduleResult> concertSchedules) {
        public static ConcertScheduleResponse of(List<ConcertScheduleResult> concertSchedules) {
            return new ConcertScheduleResponse(concertSchedules);
        }
    }

    public record ConcertSeatsResponse(ConcertSeatResult concertSeatInfo) {
        public static ConcertSeatsResponse of(ConcertSeatResult concertSeatInfo) {
            return new ConcertSeatsResponse(concertSeatInfo);
        }
    }

    public record ReservationResponse(Long id,
                                      Long userId,
                                      List<Long> seatIds,
                                      ReservationStatus status,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt
    ) {
        public static ReservationResponse of(ReservationResult reservationResult) {
            return new ReservationResponse(reservationResult.id(),
                    reservationResult.userId(),
                    reservationResult.seatIds(),
                    reservationResult.status(),
                    reservationResult.createdAt(),
                    reservationResult.updatedAt());
        }

    }
}
