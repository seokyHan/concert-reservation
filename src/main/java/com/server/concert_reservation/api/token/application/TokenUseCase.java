package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.domain.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.dto.command.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import java.util.List;

public interface TokenUseCase {

    Token createToken(TokenCommand command);
    TokenInfo getWaitingToken(String token, Long userId);
    public void checkActivatedToken(String token);
    public void activateToken(String token);
    public List<Token> getWaitingToken(int activeCount);
    public List<Token> getWaitingTokenToBeExpired(int minutes);
    public void expireToken(String token);
}
