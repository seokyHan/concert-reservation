package com.server.concert_reservation.api_backup.token.application;

import com.server.concert_reservation.api_backup.token.application.dto.TokenCommand;
import com.server.concert_reservation.domain.queue_token.model.Token;

public interface TokenCommandUseCase {

    Token createToken(TokenCommand command);

    void checkActivatedToken(String token);

    void activateToken(String token);

    void expireToken(String token);
}
