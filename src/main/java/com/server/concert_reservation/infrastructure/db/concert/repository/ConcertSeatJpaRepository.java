package com.server.concert_reservation.infrastructure.db.concert.repository;

import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeatEntity, Long> {
    List<ConcertSeatEntity> findByConcertScheduleId(Long concertScheduleId);

}
