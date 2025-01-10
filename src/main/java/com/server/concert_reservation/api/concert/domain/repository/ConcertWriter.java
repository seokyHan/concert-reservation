package com.server.concert_reservation.api.concert.domain.repository;


import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;

import java.util.List;

public interface ConcertWriter {
    List<ConcertSeat> saveAll(List<ConcertSeat> concertSeats);
    Reservation saveReservation(Reservation reservation);
    ConcertSchedule save(ConcertSchedule concertSchedule);
    Concert save(Concert concert);
}
