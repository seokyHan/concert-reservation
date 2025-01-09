package com.server.concert_reservation.api.concert.infrastructure.repository;

import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {
}
