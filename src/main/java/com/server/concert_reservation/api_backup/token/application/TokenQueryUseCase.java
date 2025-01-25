package com.server.concert_reservation.api_backup.token.application;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;

import java.util.List;

public interface TokenQueryUseCase {

    TokenInfo getWaitingToken(String token, Long userId);

    List<QueueToken> getWaitingToken(int activeCount);

    List<QueueToken> getWaitingTokensToBeExpired(int minutes);
}
