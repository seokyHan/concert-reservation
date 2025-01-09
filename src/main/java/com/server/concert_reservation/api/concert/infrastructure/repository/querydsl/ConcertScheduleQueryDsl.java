package com.server.concert_reservation.api.concert.infrastructure.repository.querydsl;

import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertScheduleEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleQueryDsl {

    List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime);
}
