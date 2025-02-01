package com.server.concert_reservation.infrastructure.queue_token.repository.querydsl;

import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueTokenQueryDsl {

    Optional<QueueTokenEntity> findLatestActivatedToken();

    List<QueueTokenEntity> findWaitingToken(int activationCount);

    List<QueueTokenEntity> findWaitingTokenToBeExpired(LocalDateTime expiredAt);
}
