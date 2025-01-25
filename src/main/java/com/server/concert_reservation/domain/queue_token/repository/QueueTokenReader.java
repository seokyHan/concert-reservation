package com.server.concert_reservation.domain.queue_token.repository;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;

import java.util.List;
import java.util.Optional;

public interface QueueTokenReader {
    QueueToken getByToken(String token);

    QueueToken getByUserIdAndToken(Long userId, String token);

    Optional<QueueToken> getLatestActiveToken();

    List<QueueToken> getWaitingTokensToBeExpired(int minutes);

    List<QueueToken> getWaitingToken(int activeCount);
}
