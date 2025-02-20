package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationOutbox {

    private String messageId;
    private String kafkaMessageId;
    private OutboxStatus status;
    private String payload;
    private int retryCount;
    private LocalDateTime retryAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReservationOutbox of(String messageId,
                                       String kafkaMessageId,
                                       OutboxStatus status,
                                       String payload,
                                       int retryCount,
                                       LocalDateTime retryAt,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return new ReservationOutbox(messageId, kafkaMessageId, status, payload, retryCount, retryAt, createdAt, updatedAt);
    }

    public ReservationOutboxEntity toEntity(ReservationOutbox reservationOutbox) {
        return ReservationOutboxEntity.builder()
                .messageId(reservationOutbox.getMessageId())
                .kafkaMessageId(reservationOutbox.getKafkaMessageId())
                .status(reservationOutbox.getStatus())
                .payload(reservationOutbox.getPayload())
                .retryCount(reservationOutbox.getRetryCount())
                .retryAt(reservationOutbox.getRetryAt())
                .build();

    }

    public void publish() {
        this.status = OutboxStatus.PUBLISHED;
    }
}
