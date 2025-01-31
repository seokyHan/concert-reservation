package com.server.concert_reservation.application.concert;

import com.server.concert_reservation.application.concert.dto.ReservationResult;
import com.server.concert_reservation.domain.concert.service.ConcertCommandService;
import com.server.concert_reservation.domain.concert.service.ConcertQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertSchedulerUseCase {

    private final ConcertCommandService concertCommandService;
    private final ConcertQueryService concertQueryService;

    @Transactional
    public void cancelReserveSeats(Long reservationId) {
        concertCommandService.cancelTemporaryReservation(reservationId);
    }

    public List<ReservationResult> getTemporaryReservationByExpired(int minute) {
        val reservations = concertQueryService.findTemporaryReservationByExpired(minute);
        return reservations.stream()
                .map(ReservationResult::from)
                .collect(Collectors.toList());
    }

}
