package com.server.concert_reservation.infrastructure.db.concert.repository.core;

import com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertScheduleEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertSeatEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;
import com.server.concert_reservation.infrastructure.db.concert.repository.ConcertScheduleJpaRepository;
import com.server.concert_reservation.infrastructure.db.concert.repository.ConcertSeatJpaRepository;
import com.server.concert_reservation.infrastructure.db.concert.repository.ReservationJpaRepository;
import com.server.concert_reservation.infrastructure.db.concert.repository.ReservationOutboxJpaRepository;
import com.server.concert_reservation.infrastructure.db.concert.repository.querydsl.ConcertQueryDsl;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import com.server.concert_reservation.interfaces.web.support.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ConcertCoreReader implements ConcertReader {
    private final TimeManager timeManager;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationOutboxJpaRepository reservationOutboxJpaRepository;
    private final ConcertQueryDsl concertQueryDsl;

    @Override
    public ConcertSchedule getConcertScheduleById(Long concertScheduleId) {
        return concertScheduleJpaRepository.findById(concertScheduleId)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_SCHEDULE_NOT_FOUND))
                .toDomain();
    }

    @Override
    public List<ConcertSchedule> getConcertScheduleByConcertId(Long concertId) {
        val concertSchedules = concertQueryDsl.findGetAvailableConcertSchedule(concertId, timeManager.now());

        return concertSchedules
                .stream()
                .map(ConcertScheduleEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertSeat getConcertSeatById(Long concertSeatId) {
        return concertSeatJpaRepository.findById(concertSeatId)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_SEAT_NOT_FOUND))
                .toDomain();
    }

    @Override
    public Reservation getReservationById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.RESERVATION_NOT_FOUND))
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

        return reservationJpaRepository.findTemporaryReservationsExpired(expirationTime).stream()
                .map(ReservationEntity::toDomain)
                .toList();
    }

    @Override
    public List<ReservationOutbox> getPendingReservationOutboxMessage(int limit) {
        val limitTime = timeManager.now().minusMinutes(limit);

        return concertQueryDsl.findAllReservationPendingOutboxMessage(limitTime).stream()
                .map(ReservationOutboxEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationOutbox getReservationOutboxByKafkaMessageId(String kafkaMessageId) {
        return reservationOutboxJpaRepository.findByKafkaMessageId(kafkaMessageId)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.RESERVATION_OUTBOX_NOT_FOUND));
    }
}
