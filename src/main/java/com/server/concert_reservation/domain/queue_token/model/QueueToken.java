package com.server.concert_reservation.domain.queue_token.model;

import com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QueueToken {

    private Long id;
    private Long userId;
    private String token;
    private QueueTokenStatus status;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QueueToken of(Long id,
                                Long userId,
                                String token,
                                QueueTokenStatus status,
                                LocalDateTime activatedAt,
                                LocalDateTime expiredAt,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        return new QueueToken(id, userId, token, status, activatedAt, expiredAt, createdAt, updatedAt);
    }

    public boolean isWaiting() {
        return this.status == WAITING;
    }

    public void activate(LocalDateTime activatedAt) {
        if (this.status == ACTIVE) {
            throw new CustomException(QueueTokenErrorCode.ALREADY_ACTIVATED);
        }

        if (this.status == EXPIRED) {
            throw new CustomException(QueueTokenErrorCode.TOKEN_EXPIRED);
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
            throw new CustomException(QueueTokenErrorCode.TOKEN_EXPIRED);
        }

        if (this.status != ACTIVE) {
            throw new CustomException(QueueTokenErrorCode.TOKEN_NOT_ACTIVATED);
        }

    }

    public QueueTokenEntity toEntity(QueueToken token) {
        return QueueTokenEntity.builder()
                .id(token.getId())
                .userId(token.getUserId())
                .token(token.getToken())
                .status(token.getStatus())
                .activatedAt(token.getActivatedAt())
                .expiredAt(token.getExpiredAt())
                .build();
    }
}
