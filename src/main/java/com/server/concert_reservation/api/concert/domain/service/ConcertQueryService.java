package com.server.concert_reservation.api.concert.domain.service;

import com.server.concert_reservation.api.concert.application.GetConcertUseCase;
import com.server.concert_reservation.api.concert.domain.model.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.domain.model.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConcertQueryService implements GetConcertUseCase {

    private final ConcertReader concertReader;


    /**
     * 예약 가능 콘서트 스케줄 조회
     */
    @Override
    public List<ConcertScheduleInfo> getAvailableConcertSchedules(Long concertId, LocalDateTime dateTime) {
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
    @Override
    public ConcertSeatInfo getAvailableConcertSeats(Long concertScheduleId) {
        val concertSchedule = concertReader.getConcertScheduleById(concertScheduleId);
        val availableSeatList = concertReader.getConcertSeatByScheduleId(concertScheduleId)
                .stream()
                .filter(ConcertSeat::isAvailable)
                .collect(Collectors.toList());

        return ConcertSeatInfo.of(concertSchedule, availableSeatList);
    }

    @Override
    public Reservation getReservation(Long reservationId) {
        return concertReader.getReservationById(reservationId);
    }

    @Override
    public List<Reservation> getTemporaryReservationByExpired(int minute) {
        return concertReader.getTemporaryReservationsExpired(minute);
    }
}
