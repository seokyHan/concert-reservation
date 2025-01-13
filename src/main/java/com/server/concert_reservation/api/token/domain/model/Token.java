package com.server.concert_reservation.api.token.domain.model;

import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus;
import com.server.concert_reservation.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.*;
import static com.server.concert_reservation.common.exception.code.TokenErrorCode.*;

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
    private LocalDateTime lastActionedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Token of(Long id,
                           Long userId,
                           String token,
                           TokenStatus status,
                           LocalDateTime activatedAt,
                           LocalDateTime expiredAt,
                           LocalDateTime lastActionedAt,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        return new Token(id, userId, token, status, activatedAt, expiredAt, lastActionedAt, createdAt, updatedAt);
    }

    public Token(Long userId, String token) {
        this.userId = userId;
        this.token = token;
        this.status = WAITING;
    }

    public boolean isWaiting() {
        return this.status == WAITING;
    }

    public boolean isActivated() {
        return this.status == ACTIVE;
    }

    public boolean isExpired() {
        return this.status == EXPIRED;
    }

    public void activate(LocalDateTime activatedAt) {
        if (this.isActivated()) {
            throw new CustomException(ALREADY_ACTIVATED);
        }

        if (this.isExpired()) {
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
        if (this.isExpired()) {
            throw new CustomException(TOKEN_EXPIRED);
        }

        if (!this.isActivated()) {
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
                .lastActionedAt(token.getLastActionedAt())
                .build();
    }
}
