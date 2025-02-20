package com.server.concert_reservation.domain.concert.repository;

import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.model.ReservationOutbox;

import java.util.List;

public interface ConcertReader {
    ConcertSchedule getConcertScheduleById(Long concertScheduleId);

    List<ConcertSchedule> getConcertScheduleByConcertId(Long concertId);

    ConcertSeat getConcertSeatById(Long concertSeatId);

    Reservation getReservationById(Long reservationId);

    List<ConcertSeat> getConcertSeatsByIds(List<Long> seatIds);

    List<ConcertSeat> getConcertSeatByScheduleId(Long concertScheduleId);

    List<Reservation> getTemporaryReservationsExpired(int minutes);

    List<ReservationOutbox> getPendingReservationOutboxMessage(int limit);

    ReservationOutbox getReservationOutboxByKafkaMessageId(String kafkaMessageId);

}
