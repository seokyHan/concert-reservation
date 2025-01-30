package com.server.concert_reservation.application.concert;

import com.server.concert_reservation.api_backup.user.application.UserQueryService;
import com.server.concert_reservation.application.concert.dto.ConcertScheduleResult;
import com.server.concert_reservation.application.concert.dto.ConcertSeatResult;
import com.server.concert_reservation.application.concert.dto.ReservationCommand;
import com.server.concert_reservation.application.concert.dto.ReservationResult;
import com.server.concert_reservation.domain.concert.service.ConcertCommandService;
import com.server.concert_reservation.domain.concert.service.ConcertQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertFacade {

    private final ConcertCommandService concertCommandService;
    private final ConcertQueryService concertQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public ReservationResult reserveSeats(ReservationCommand command) {
        val user = userQueryService.getUser(command.userId());
        concertCommandService.checkConcertSchedule(command.concertScheduleId(), command.dateTime());
        val reservation = concertCommandService.reserveSeats(user.id(), command.seatIds());

        return ReservationResult.of(reservation);
    }

    @Transactional
    public void cancelReserveSeats(Long reservationId) {
        concertCommandService.cancelTemporaryReservation(reservationId);
    }


    public List<ConcertScheduleResult> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
        val concertSchedules = concertQueryService.findAvailableConcertSchedules(concertId, dateTime);
        return concertSchedules.stream()
                .map(ConcertScheduleResult::of)
                .collect(Collectors.toList());
    }

    public ConcertSeatResult getAvailableConcertSeats(Long concertScheduleId) {
        val concertSeat = concertQueryService.findAvailableConcertSeats(concertScheduleId);
        return ConcertSeatResult.of(concertSeat);
    }

    public List<ReservationResult> getTemporaryReservationByExpired(int minute) {
        val reservations = concertQueryService.findTemporaryReservationByExpired(minute);
        return reservations.stream()
                .map(ReservationResult::of)
                .collect(Collectors.toList());
    }
}
