package com.server.concert_reservation.infrastructure.queue_token.repository;

import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface QueueTokenJpaRepository extends JpaRepository<QueueTokenEntity, Long> {

    Optional<QueueTokenEntity> findByUserIdAndToken(Long userId, String token);

    Optional<QueueTokenEntity> findByToken(String token);


}
