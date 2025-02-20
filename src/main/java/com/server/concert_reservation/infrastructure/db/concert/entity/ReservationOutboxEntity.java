package com.server.concert_reservation.infrastructure.db.concert.entity;

import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.infrastructure.db.auditing.BaseTimeEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation_outbox")
public class ReservationOutboxEntity extends BaseTimeEntity {

    @Id
    private String messageId;
    private String kafkaMessageId;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    @Column(columnDefinition = "TEXT")
    private String payload;

    private int retryCount;

    private LocalDateTime retryAt;

    @Builder
    public ReservationOutboxEntity(String messageId, String kafkaMessageId, OutboxStatus status, String payload, int retryCount, LocalDateTime retryAt) {
        this.messageId = messageId;
        this.kafkaMessageId = kafkaMessageId;
        this.status = status;
        this.payload = payload;
        this.retryCount = retryCount;
        this.retryAt = retryAt;
    }


    public ReservationOutbox toDomain() {
        return ReservationOutbox.of(messageId, kafkaMessageId, status, payload, retryCount, retryAt, createdAt, updatedAt);
    }
}
