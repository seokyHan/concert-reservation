package com.server.concert_reservation.infrastructure.db.concert.repository;

import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutboxEntity, String> {
    Optional<ReservationOutbox> findByKafkaMessageId(String kafkaMessageId);
}
