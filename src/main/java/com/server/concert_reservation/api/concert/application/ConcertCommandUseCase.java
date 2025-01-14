package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;

public interface ConcertCommandUseCase {
    ReservationInfo temporaryReserveConcert(ReservationCommand command);
    void completeReservation(Long reservationId);
    void cancelTemporaryReservation(Long reservationId);
}
