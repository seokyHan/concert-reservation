package com.server.concert_reservation.infrastructure.concert.repository;

import com.server.concert_reservation.infrastructure.concert.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {
    List<ConcertScheduleEntity> findByConcertId(Long concertId);
}
