package com.server.concert_reservation.infrastructure.concert.repository.querydsl;

import com.server.concert_reservation.infrastructure.concert.entity.ConcertScheduleEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleQueryDsl {

    List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime);
}
