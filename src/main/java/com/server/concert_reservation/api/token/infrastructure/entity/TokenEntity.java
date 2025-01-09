package com.server.concert_reservation.api.token.infrastructure.entity;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.infrastructure.types.TokenStatus;
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
@Table(name = "token")
public class TokenEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userId")
    private Long userId;
    @Column(name = "token")
    private String token;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TokenStatus status;
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "last_actioned_at")
    private LocalDateTime lastActionedAt;

    @Builder
    public TokenEntity(Long id, Long userId, String token, TokenStatus status, LocalDateTime activatedAt, LocalDateTime expiredAt, LocalDateTime lastActionedAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.activatedAt = activatedAt;
        this.expiredAt = expiredAt;
        this.lastActionedAt = lastActionedAt;
    }



    public Token toDomain() {
        return Token.of(id, userId, token, status, activatedAt, expiredAt, lastActionedAt, createdAt, updatedAt);
    }
}
