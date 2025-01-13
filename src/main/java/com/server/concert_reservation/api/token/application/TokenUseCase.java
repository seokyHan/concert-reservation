package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.domain.model.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.model.dto.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import java.util.List;

public interface TokenUseCase {

    Token createToken(TokenCommand command);
    TokenInfo getWaitingToken(String token, Long userId);
    void checkActivatedToken(String token);
    void activateToken(String token);
    List<Token> getWaitingToken(int activeCount);
    List<Token> getWaitingTokenToBeExpired(int minutes);
    void expireToken(String token);
}
