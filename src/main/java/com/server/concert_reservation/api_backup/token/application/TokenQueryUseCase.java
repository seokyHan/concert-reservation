package com.server.concert_reservation.api_backup.token.application;

import com.server.concert_reservation.domain.queue_token.model.Token;
import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;

import java.util.List;

public interface TokenQueryUseCase {

    TokenInfo getWaitingToken(String token, Long userId);

    List<Token> getWaitingToken(int activeCount);

    List<Token> getWaitingTokensToBeExpired(int minutes);
}
