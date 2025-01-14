package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.application.dto.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;

public interface TokenCommandUseCase {

    Token createToken(TokenCommand command);
    void checkActivatedToken(String token);
    void activateToken(String token);
    void expireToken(String token);
}
