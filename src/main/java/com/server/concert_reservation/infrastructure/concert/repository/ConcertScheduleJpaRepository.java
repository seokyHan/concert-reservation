package com.server.concert_reservation.infrastructure.concert.repository;

import com.server.concert_reservation.infrastructure.concert.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {
}
