package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.domain.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.domain.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;


public interface GetConcertUseCase {

    List<ConcertScheduleInfo> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime);
    ConcertSeatInfo getAvailableConcertSeats(Long concertScheduleId);
    Reservation getReservation(Long reservationId);
    List<Reservation> getTemporaryReservationByExpired(int minute);
}
