package com.server.concert_reservation.api.concert.domain.repository;

import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertReader {
    ConcertSchedule getConcertScheduleById(Long concertScheduleId);
    List<ConcertSchedule> getConcertScheduleByConcertIdAndDate(Long concertId, LocalDateTime dateTime);
    ConcertSeat getConcertSeatById(Long concertSeatId);
    Reservation getReservationById(Long reservationId);
    List<ConcertSeat> getConcertSeatsByIds(List<Long> seatIds);
    List<ConcertSeat> getConcertSeatByScheduleId(Long concertScheduleId);
    List<Reservation> getTemporaryReservationsExpired(int minutes);
}
