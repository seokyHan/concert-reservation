package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.domain.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.domain.dto.command.ReservationCommand;
import org.springframework.stereotype.Component;

public interface ReservationUseCase {
    ReservationInfo temporaryReserveConcert(ReservationCommand command);
    void completeReservation(Long reservationId);
    void cancelTemporaryReservation(Long reservationId);
}
