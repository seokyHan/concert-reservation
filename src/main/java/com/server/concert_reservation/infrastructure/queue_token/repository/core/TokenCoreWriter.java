package com.server.concert_reservation.infrastructure.queue_token.repository.core;


import com.server.concert_reservation.domain.queue_token.model.Token;
import com.server.concert_reservation.domain.queue_token.repository.TokenWriter;
import com.server.concert_reservation.infrastructure.queue_token.repository.TokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class TokenCoreWriter implements TokenWriter {

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public Token save(Token token) {
        return tokenJpaRepository.save(token.toEntity(token)).toDomain();
    }
}
