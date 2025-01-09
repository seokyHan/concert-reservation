package com.server.concert_reservation.api.token.presentation.dto;

import com.server.concert_reservation.api.token.domain.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.infrastructure.types.TokenStatus;

import java.time.LocalDateTime;

public class TokenHttp {

    public record TokenRequest(Long userId, String token) {}

    public record TokenResponse(Long id,
                                Long userId,
                                String token,
                                TokenStatus status,
                                LocalDateTime activatedAt,
                                LocalDateTime expiredAt,
                                LocalDateTime lastActionedAt,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        public static TokenResponse of (Token token) {
            return new TokenResponse(token.getId(),
                    token.getUserId(),
                    token.getToken(),
                    token.getStatus(),
                    token.getActivatedAt(),
                    token.getExpiredAt(),
                    token.getLastActionedAt(),
                    token.getCreatedAt(),
                    token.getUpdatedAt());
        }
        public static TokenResponse of (TokenInfo tokenInfo) {
            return new TokenResponse(tokenInfo.id(),
                    tokenInfo.userId(),
                    tokenInfo.token(),
                    tokenInfo.status(),
                    tokenInfo.activatedAt(),
                    tokenInfo.expiredAt(),
                    tokenInfo.lastActionedAt(),
                    tokenInfo.createdAt(),
                    tokenInfo.updatedAt());
        }
    }
}
