package com.server.concert_reservation.interfaces.web.queue_token.dto;

import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
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
                                     LocalDateTime updatedAt) {
        public static QueueTokenResponse of(QueueToken token) {
            return new QueueTokenResponse(token.getId(),
                    token.getUserId(),
                    token.getToken(),
                    token.getStatus(),
                    token.getActivatedAt(),
                    token.getExpiredAt(),
                    token.getCreatedAt(),
                    token.getUpdatedAt());
        }

        public static QueueTokenResponse of(TokenInfo tokenInfo) {
            return new QueueTokenResponse(tokenInfo.id(),
                    tokenInfo.userId(),
                    tokenInfo.token(),
                    tokenInfo.status(),
                    tokenInfo.activatedAt(),
                    tokenInfo.expiredAt(),
                    tokenInfo.createdAt(),
                    tokenInfo.updatedAt());
        }
    }
}
