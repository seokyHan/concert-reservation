package com.server.concert_reservation.infrastructure.queue_token.repository.core;


import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.infrastructure.queue_token.repository.TokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class TokenCoreWriter implements QueueTokenWriter {

    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public QueueToken save(QueueToken token) {
        return tokenJpaRepository.save(token.toEntity(token)).toDomain();
    }
}
