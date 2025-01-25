package com.server.concert_reservation.api_backup.token.application.dto;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;

import java.time.LocalDateTime;

public record TokenInfo(Long id,
                        Long userId,
                        String token,
                        QueueTokenStatus status,
                        LocalDateTime activatedAt,
                        LocalDateTime expiredAt,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt,
                        Long waitingNumber
) {

    public static TokenInfo from(QueueToken token, Long waitingNumber) {
        return new TokenInfo(token.getId(),
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

}
