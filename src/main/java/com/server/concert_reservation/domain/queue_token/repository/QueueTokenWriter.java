package com.server.concert_reservation.domain.queue_token.repository;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import org.springframework.stereotype.Repository;


@Repository
public interface QueueTokenWriter {
    QueueToken save(QueueToken token);
}
