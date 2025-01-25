package com.server.concert_reservation.api_backup.concert.application;

import com.server.concert_reservation.api_backup.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.api_backup.concert.application.dto.ReservationCommand;

public interface ConcertCommandUseCase {
    ReservationInfo temporaryReserveConcert(ReservationCommand command);
    void completeReservation(Long reservationId);
    void cancelTemporaryReservation(Long reservationId);
}
