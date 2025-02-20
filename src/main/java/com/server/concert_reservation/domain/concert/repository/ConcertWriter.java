package com.server.concert_reservation.domain.concert.repository;


import com.server.concert_reservation.domain.concert.model.*;

import java.util.List;

public interface ConcertWriter {
    List<ConcertSeat> saveAll(List<ConcertSeat> concertSeats);

    Reservation saveReservation(Reservation reservation);

    ConcertSchedule saveConcertSchedule(ConcertSchedule concertSchedule);

    Concert saveConcert(Concert concert);

    ReservationOutbox saveReservationOutbox(ReservationOutbox reservationOutbox);
}
