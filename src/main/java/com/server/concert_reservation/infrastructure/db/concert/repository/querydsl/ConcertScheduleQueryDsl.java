package com.server.concert_reservation.infrastructure.db.concert.repository.querydsl;

import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertScheduleEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleQueryDsl {

    List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime);
}
