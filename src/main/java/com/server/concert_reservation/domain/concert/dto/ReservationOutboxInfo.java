package com.server.concert_reservation.domain.concert.dto;

import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;

import java.time.LocalDateTime;

public record ReservationOutboxInfo(String messageId,
                                    String kafkaMessageId,
                                    OutboxStatus status,
                                    String payload,
                                    int retryCount,
                                    LocalDateTime retryAt,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt
) {

    public static ReservationOutboxInfo from(ReservationOutbox reservationOutbox) {
        return new ReservationOutboxInfo(reservationOutbox.getMessageId(),
                reservationOutbox.getKafkaMessageId(),
                reservationOutbox.getStatus(),
                reservationOutbox.getPayload(),
                reservationOutbox.getRetryCount(),
                reservationOutbox.getRetryAt(),
                reservationOutbox.getCreatedAt(),
                reservationOutbox.getUpdatedAt());
    }
}
