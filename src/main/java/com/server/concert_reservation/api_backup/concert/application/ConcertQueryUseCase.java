package com.server.concert_reservation.api_backup.concert.application;

import com.server.concert_reservation.api_backup.concert.application.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api_backup.concert.application.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;


public interface ConcertQueryUseCase {

    List<ConcertScheduleInfo> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime);

    ConcertSeatInfo getAvailableConcertSeats(Long concertScheduleId);

    Reservation getReservation(Long reservationId);

    List<Reservation> getTemporaryReservationByExpired(int minute);
}
