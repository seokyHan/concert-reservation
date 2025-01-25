package com.server.concert_reservation.infrastructure.queue_token.entity;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "queue_token")
public class QueueTokenEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userId")
    private Long userId;
    @Column(name = "token")
    private String token;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private QueueTokenStatus status;
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public QueueTokenEntity(Long id, Long userId, String token, QueueTokenStatus status, LocalDateTime activatedAt, LocalDateTime expiredAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.activatedAt = activatedAt;
        this.expiredAt = expiredAt;
    }


    public QueueToken toDomain() {
        return QueueToken.of(id, userId, token, status, activatedAt, expiredAt, createdAt, updatedAt);
    }
}
