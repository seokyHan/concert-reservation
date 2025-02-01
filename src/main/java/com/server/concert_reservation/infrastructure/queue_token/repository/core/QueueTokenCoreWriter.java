package com.server.concert_reservation.infrastructure.queue_token.repository.core;


import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.infrastructure.queue_token.repository.QueueTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class QueueTokenCoreWriter implements QueueTokenWriter {

    private final QueueTokenJpaRepository queueTokenJpaRepository;

    @Override
    public QueueToken save(QueueToken token) {
        return queueTokenJpaRepository.save(token.toEntity(token)).toDomain();
    }
}
