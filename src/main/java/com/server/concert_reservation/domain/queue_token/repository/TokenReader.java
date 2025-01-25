package com.server.concert_reservation.domain.queue_token.repository;

import com.server.concert_reservation.domain.queue_token.model.Token;

import java.util.List;
import java.util.Optional;

public interface TokenReader {
    Token getByToken(String token);

    Token getByUserIdAndToken(Long userId, String token);

    Optional<Token> getLatestActiveToken();

    List<Token> getWaitingTokensToBeExpired(int minutes);

    List<Token> getWaitingToken(int activeCount);
}
