package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationOutboxInfo;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ConcertQueryService {

    private final ConcertReader concertReader;

    @Cacheable(value = "availableConcertSchedule", key = "#concertId")
    public List<ConcertScheduleInfo> findAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
        val concertSchedules = concertReader.getConcertScheduleByConcertId(concertId);

        return concertSchedules.stream()
                .filter(schedule -> schedule.isAvailableReservePeriod(dateTime))
                .map(ConcertScheduleInfo::from)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "availableConcertSeats", key = "#concertScheduleId")
    public List<ConcertSeatInfo> findAvailableConcertSeats(Long concertScheduleId) {
        val availableSeatList = concertReader.getConcertSeatByScheduleId(concertScheduleId)
                .stream()
                .filter(concertSeat -> concertSeat.getStatus() == SeatStatus.AVAILABLE)
                .map(ConcertSeatInfo::from)
                .collect(Collectors.toList());

        return availableSeatList;
    }

    public ConcertScheduleInfo findConcertSchedule(Long concertScheduleId) {
        val concertSchedule = concertReader.getConcertScheduleById(concertScheduleId);

        return ConcertScheduleInfo.from(concertSchedule);
    }

    public List<ReservationInfo> findTemporaryReservationByExpired(int minute) {
        val reservations = concertReader.getTemporaryReservationsExpired(minute);

        return reservations.stream()
                .map(ReservationInfo::from)
                .collect(Collectors.toList());
    }

    public ReservationInfo findTemporaryReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.isTemporaryReserved();

        return ReservationInfo.from(reservation);
    }

    public List<ReservationOutboxInfo> findPendingReservationOutboxMessage(int limitTime) {
        val reservationOutboxes = concertReader.getPendingReservationOutboxMessage(limitTime);

        return reservationOutboxes.stream()
                .map(ReservationOutboxInfo::from)
                .collect(Collectors.toList());
    }
}
