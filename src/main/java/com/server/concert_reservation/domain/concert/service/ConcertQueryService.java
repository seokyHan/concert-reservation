package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConcertQueryService {

    private final ConcertReader concertReader;


    /**
     * 예약 가능 콘서트 스케줄 조회
     */
    public List<ConcertScheduleInfo> findAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
        val concertSchedules = concertReader.getConcertScheduleByConcertIdAndDate(concertId, dateTime);

        return concertSchedules.isEmpty() ?
                List.of() :
                concertSchedules.stream()
                        .map(ConcertScheduleInfo::of)
                        .collect(Collectors.toList());
    }

    /**
     * 예약 가능 콘서트 좌석 조회
     */
    public ConcertSeatInfo findAvailableConcertSeats(Long concertScheduleId) {
        val concertSchedule = concertReader.getConcertScheduleById(concertScheduleId);
        val availableSeatList = concertReader.getConcertSeatByScheduleId(concertScheduleId)
                .stream()
                .filter(concertSeat -> concertSeat.getStatus() == SeatStatus.AVAILABLE)
                .collect(Collectors.toList());

        return ConcertSeatInfo.of(concertSchedule, availableSeatList);
    }

    public Reservation findReservation(Long reservationId) {
        return concertReader.getReservationById(reservationId);
    }

    public List<ReservationInfo> findTemporaryReservationByExpired(int minute) {
        val reservations = concertReader.getTemporaryReservationsExpired(minute);

        return reservations.stream()
                .map(ReservationInfo::from)
                .collect(Collectors.toList());
    }
}
