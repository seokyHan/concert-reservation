package com.server.concert_reservation.api.concert.infrastructure.repository.core;

import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertScheduleEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertSeatEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.ReservationEntity;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertScheduleJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertSeatJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ReservationJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.querydsl.ConcertScheduleQueryDsl;
import com.server.concert_reservation.common.exception.CustomException;
import com.server.concert_reservation.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.concert_reservation.common.exception.code.ConcertErrorCode.*;

@RequiredArgsConstructor
@Repository
public class ConcertCoreReader implements ConcertReader {
    private final TimeManager timeManager;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final ConcertScheduleQueryDsl concertScheduleQueryDsl;

    @Override
    public ConcertSchedule getConcertScheduleById(Long concertScheduleId) {
        return concertScheduleJpaRepository.findById(concertScheduleId)
                .orElseThrow(() -> new CustomException(CONCERT_SCHEDULE_NOT_FOUND))
                .toDomain();
    }

    @Override
    public List<ConcertSchedule> getConcertScheduleByConcertIdAndDate(Long concertId, LocalDateTime dateTime) {
        return concertScheduleQueryDsl.findGetAvailableConcertSchedule(concertId, dateTime)
                .stream()
                .map(ConcertScheduleEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertSeat getConcertSeatById(Long concertSeatId) {
        return concertSeatJpaRepository.findById(concertSeatId)
                .orElseThrow(() -> new CustomException(CONCERT_SEAT_NOT_FOUND))
                .toDomain();
    }

    @Override
    public Reservation getReservationById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND))
                .toDomain();
    }

    @Override
    public List<ConcertSeat> getConcertSeatsByIds(List<Long> seatIds) {
        return concertSeatJpaRepository.findAllById(seatIds).stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public List<ConcertSeat> getConcertSeatByScheduleId(Long concertScheduleId) {
        return concertSeatJpaRepository.findByConcertScheduleId(concertScheduleId)
                .stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> getTemporaryReservationsExpired(int minutes) {
        val expirationTime = timeManager.now().minusMinutes(minutes);

        return reservationJpaRepository.findTemporaryReservationsToBeExpired(expirationTime).stream()
                .map(ReservationEntity::toDomain)
                .toList();
    }

}
