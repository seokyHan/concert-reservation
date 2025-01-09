package com.server.concert_reservation.api.concert.domain.repository;


import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;

import java.util.List;

public interface ConcertWriter {
    List<ConcertSeat> saveAll(List<ConcertSeat> concertSeats);
    Reservation saveReservation(Reservation reservation);
}
