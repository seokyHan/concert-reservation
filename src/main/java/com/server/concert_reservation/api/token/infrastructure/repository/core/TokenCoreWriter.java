package com.server.concert_reservation.api.token.infrastructure.repository.core;


import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenWriter;
import com.server.concert_reservation.api.token.infrastructure.repository.TokenJpaRepository;
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
