package com.server.concert_reservation.api.token.domain.repository;

import com.server.concert_reservation.api.token.domain.model.Token;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenWriter {
    Token save(Token token);
}
