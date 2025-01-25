package com.server.concert_reservation.domain.queue_token.repository;

import com.server.concert_reservation.domain.queue_token.model.Token;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenWriter {
    Token save(Token token);
}
