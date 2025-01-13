package com.server.concert_reservation.api.concert.presentation.dto;

import com.server.concert_reservation.api.concert.domain.model.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.domain.model.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.infrastructure.types.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttp {

    public record ConcertReservationRequest(Long userId,
                                            Long concertScheduleId,
                                            List<Long> seatIds,
                                            LocalDateTime dateTime
    ) { }

    public record ConcertScheduleResponse (List<ConcertScheduleInfo> concertSchedules) {
        public static ConcertScheduleResponse of(List<ConcertScheduleInfo> concertSchedules) {
            return new ConcertScheduleResponse(concertSchedules);
        }
    }

    public record ConcertSeatsResponse (ConcertSeatInfo concertSeatInfo) {
        public static ConcertSeatsResponse of(ConcertSeatInfo concertSeatInfo) {
            return new ConcertSeatsResponse(concertSeatInfo);
        }
    }

    public record ReservationResponse (Long id,
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
