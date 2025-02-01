package com.server.concert_reservation.interfaces.web.queue_token.dto;

import com.server.concert_reservation.application.queue_token.dto.QueueTokenResult;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;

import java.time.LocalDateTime;

public class QueueTokenHttpResponse {

    public record QueueTokenResponse(Long id,
                                     Long userId,
                                     String token,
                                     QueueTokenStatus status,
                                     LocalDateTime activatedAt,
                                     LocalDateTime expiredAt,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt,
                                     Long waitingNumber
    ) {
        public static QueueTokenResponse of(QueueTokenResult queueToken) {
            return new QueueTokenResponse(queueToken.id(),
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
}
