package com.server.concert_reservation.api.concert.infrastructure.repository;

import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {
}
