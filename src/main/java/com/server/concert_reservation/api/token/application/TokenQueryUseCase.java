package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.application.dto.TokenInfo;

import java.util.List;

public interface TokenQueryUseCase {

    TokenInfo getWaitingToken(String token, Long userId);
    List<Token> getWaitingToken(int activeCount);
    List<Token> getWaitingTokensToBeExpired(int minutes);
}
