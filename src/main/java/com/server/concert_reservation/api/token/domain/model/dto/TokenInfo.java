package com.server.concert_reservation.api.token.domain.model.dto;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.infrastructure.types.TokenStatus;

import java.time.LocalDateTime;

public record TokenInfo(Long id,
                        Long userId,
                        String token,
                        TokenStatus status,
                        LocalDateTime activatedAt,
                        LocalDateTime expiredAt,
                        LocalDateTime lastActionedAt,
                        LocalDateTime createdAt,
                        LocalDateTime updatedAt,
                        Long waitingNumber
) {

    public static TokenInfo from(Token token, Long waitingNumber) {
        return new TokenInfo(token.getId(),
                token.getUserId(),
                token.getToken(),
                token.getStatus(),
                token.getActivatedAt(),
                token.getExpiredAt(),
                token.getLastActionedAt(),
                token.getCreatedAt(),
                token.getUpdatedAt(),
                waitingNumber
        );

    }

}
