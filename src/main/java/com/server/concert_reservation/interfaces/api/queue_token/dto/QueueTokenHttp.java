package com.server.concert_reservation.interfaces.api.queue_token.dto;

import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;
import com.server.concert_reservation.domain.queue_token.model.Token;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.TokenStatus;

import java.time.LocalDateTime;

public class QueueTokenHttp {

    public record QueueTokenRequest(Long userId, String token) {
    }

    public record QueueTokenResponse(Long id,
                                     Long userId,
                                     String token,
                                     TokenStatus status,
                                     LocalDateTime activatedAt,
                                     LocalDateTime expiredAt,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
        public static QueueTokenResponse of(Token token) {
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
