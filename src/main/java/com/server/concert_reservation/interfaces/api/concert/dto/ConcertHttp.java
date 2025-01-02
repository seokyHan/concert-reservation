package com.server.concert_reservation.interfaces.api.concert.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConcertHttp {

    public record ConcertReservationRequest(Long concertSeatId) { }

    public record ConcertListResponse (List<ConcertList> ConcertList) {
        public record ConcertList(Long id,
                                  String title,
                                  String description,
                                  LocalDateTime createAt,
                                  LocalDateTime updatedAt) {}
    }

    public record ConcertScheduleResponse (List<ConcertSchedules> concertSchedules) {
        public record ConcertSchedules(Long id,
                                Long concertId,
                                LocalDateTime reservationStartAt,
                                LocalDateTime reservationEndAt) {}
    }

    public record ConcertSeatsResponse (ConcertSeats concertSeats) {
        public record ConcertSeats(Long id,
                                   Long concertScheduleId,
                                   int number,
                                   int price,
                                   boolean isReserved) {}
    }

    public record ConcertReservationResponse (ConcertReservation concertReservation) {
        public record ConcertReservation(Long id,
                                         Long concertSeatId,
                                         Long userId,
                                         String status) {}

    }
}
