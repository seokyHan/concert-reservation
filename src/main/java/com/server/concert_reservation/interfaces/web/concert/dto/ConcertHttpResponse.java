package com.server.concert_reservation.interfaces.web.concert.dto;

import com.server.concert_reservation.api_backup.concert.application.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api_backup.concert.application.dto.ConcertSeatInfo;
import com.server.concert_reservation.api_backup.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttpResponse {

    public record ConcertScheduleResponse(List<ConcertScheduleInfo> concertSchedules) {
        public static ConcertScheduleResponse of(List<ConcertScheduleInfo> concertSchedules) {
            return new ConcertScheduleResponse(concertSchedules);
        }
    }

    public record ConcertSeatsResponse(ConcertSeatInfo concertSeatInfo) {
        public static ConcertSeatsResponse of(ConcertSeatInfo concertSeatInfo) {
            return new ConcertSeatsResponse(concertSeatInfo);
        }
    }

    public record ReservationResponse(Long id,
                                      Long userId,
                                      List<ConcertSeat> seatIds,
                                      ReservationStatus status,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt
    ) {
        public static ReservationResponse of(ReservationInfo reservationInfo) {
            return new ReservationResponse(reservationInfo.id(),
                    reservationInfo.userId(),
                    reservationInfo.seatIds(),
                    reservationInfo.status(),
                    reservationInfo.createdAt(),
                    reservationInfo.updatedAt());
        }

    }
}
