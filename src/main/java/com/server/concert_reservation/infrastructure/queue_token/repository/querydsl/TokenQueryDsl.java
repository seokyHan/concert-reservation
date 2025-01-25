package com.server.concert_reservation.infrastructure.queue_token.repository.querydsl;

import com.server.concert_reservation.infrastructure.queue_token.entity.TokenEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenQueryDsl {

    Optional<TokenEntity> getLatestActivatedToken();

    List<TokenEntity> findWaitingToken(int activationCount);

    List<TokenEntity> findWaitingTokenToBeExpired(LocalDateTime expiredAt);
}
