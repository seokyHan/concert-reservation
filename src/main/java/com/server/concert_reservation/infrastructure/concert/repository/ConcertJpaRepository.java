package com.server.concert_reservation.infrastructure.concert.repository;

import com.server.concert_reservation.infrastructure.concert.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {
}
