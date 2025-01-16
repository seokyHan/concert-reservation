package com.server.concert_reservation.api.token.domain.model;

import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.*;
import static com.server.concert_reservation.api.token.domain.errorcode.TokenErrorCode.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {

    private Long id;
    private Long userId;
    private String token;
    private TokenStatus status;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Token of(Long id,
                           Long userId,
                           String token,
                           TokenStatus status,
                           LocalDateTime activatedAt,
                           LocalDateTime expiredAt,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        return new Token(id, userId, token, status, activatedAt, expiredAt, createdAt, updatedAt);
    }

    public Token(Long userId, String token) {
        this.userId = userId;
        this.token = token;
        this.status = WAITING;
    }

    public boolean isWaiting() {
        return this.status == WAITING;
    }

    public void activate(LocalDateTime activatedAt) {
        if (this.status == ACTIVE) {
            throw new CustomException(ALREADY_ACTIVATED);
        }

        if (this.status == EXPIRED) {
            throw new CustomException(TOKEN_EXPIRED);
        }

        this.status = ACTIVE;
        this.activatedAt = activatedAt;
    }

    public void expire(LocalDateTime expiredAt) {
        this.status = EXPIRED;
        this.expiredAt = expiredAt;
    }

    public void validateToken() {
        if (this.status == EXPIRED) {
            throw new CustomException(TOKEN_EXPIRED);
        }

        if (this.status != ACTIVE) {
            throw new CustomException(TOKEN_NOT_ACTIVATED);
        }

    }

    public TokenEntity toEntity(Token token) {
        return TokenEntity.builder()
                .id(token.getId())
                .userId(token.getUserId())
                .token(token.getToken())
                .status(token.getStatus())
                .activatedAt(token.getActivatedAt())
                .expiredAt(token.getExpiredAt())
                .build();
    }
}
