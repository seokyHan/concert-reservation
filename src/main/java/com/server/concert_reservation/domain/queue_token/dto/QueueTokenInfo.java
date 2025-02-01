package com.server.concert_reservation.domain.queue_token.dto;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;

import java.time.LocalDateTime;

public record QueueTokenInfo(Long id,
                             Long userId,
                             String token,
                             QueueTokenStatus status,
                             LocalDateTime activatedAt,
                             LocalDateTime expiredAt,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt,
                             Long waitingNumber
) {

    public static QueueTokenInfo of(QueueToken token, Long waitingNumber) {
        return new QueueTokenInfo(token.getId(),
                token.getUserId(),
                token.getToken(),
                token.getStatus(),
                token.getActivatedAt(),
                token.getExpiredAt(),
                token.getCreatedAt(),
                token.getUpdatedAt(),
                waitingNumber
        );
    }

    public static QueueTokenInfo from(QueueToken queueToken) {
        return new QueueTokenInfo(queueToken.getId(),
                queueToken.getUserId(),
                queueToken.getToken(),
                queueToken.getStatus(),
                queueToken.getActivatedAt(),
                queueToken.getExpiredAt(),
                queueToken.getCreatedAt(),
                queueToken.getUpdatedAt(),
                null
        );
    }

}
