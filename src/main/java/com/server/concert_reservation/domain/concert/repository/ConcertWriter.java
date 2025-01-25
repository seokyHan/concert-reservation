package com.server.concert_reservation.domain.concert.repository;


import com.server.concert_reservation.domain.concert.model.Concert;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;

import java.util.List;

public interface ConcertWriter {
    List<ConcertSeat> saveAll(List<ConcertSeat> concertSeats);

    Reservation saveReservation(Reservation reservation);

    ConcertSchedule save(ConcertSchedule concertSchedule);

    Concert save(Concert concert);
}
