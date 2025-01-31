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
public class ConcertUseCase {

    private final ConcertCommandService concertCommandService;
    private final ConcertQueryService concertQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public ReservationResult reserveSeats(ReservationCommand command) {
        val user = userQueryService.getUser(command.userId());
        val concertSchedule = concertQueryService.findConcertSchedule(command.concertScheduleId());
        val concertSeat = concertCommandService.reserveSeats(command.seatIds());
        val reservation = concertCommandService.createReservation(user.id(), concertSchedule.id(), concertSeat);

        return ReservationResult.from(reservation);
    }

    public List<ConcertScheduleResult> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
        val concertSchedules = concertQueryService.findAvailableConcertSchedules(concertId, dateTime);
        return concertSchedules.stream()
                .map(ConcertScheduleResult::from)
                .collect(Collectors.toList());
    }

    public ConcertSeatResult getAvailableConcertSeats(Long concertScheduleId) {
        val concertSchedule = concertQueryService.findConcertSchedule(concertScheduleId);
        val concertSeat = concertQueryService.findAvailableConcertSeats(concertSchedule.id());

        return ConcertSeatResult.of(concertSchedule, concertSeat);
    }

}
