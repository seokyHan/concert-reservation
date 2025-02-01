package com.server.concert_reservation.application.queue_token.dto;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;

import java.time.LocalDateTime;

public record QueueTokenResult(Long id,
                               Long userId,
                               String token,
                               QueueTokenStatus status,
                               LocalDateTime activatedAt,
                               LocalDateTime expiredAt,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt,
                               Long waitingNumber) {

    public static QueueTokenResult from(QueueTokenInfo queueToken) {
        return new QueueTokenResult(queueToken.id(),
                queueToken.userId(),
                queueToken.token(),
                queueToken.status(),
                queueToken.activatedAt(),
                queueToken.expiredAt(),
                queueToken.createdAt(),
                queueToken.updatedAt(),
                queueToken.waitingNumber()
        );
    }
}
