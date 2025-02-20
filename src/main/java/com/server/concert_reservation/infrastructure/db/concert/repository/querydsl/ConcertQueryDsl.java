package com.server.concert_reservation.infrastructure.db.concert.repository.querydsl;

import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertScheduleEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertQueryDsl {

    List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime);

    List<ReservationOutboxEntity> findAllReservationPendingOutboxMessage(LocalDateTime dateTime);
}
