package com.server.concert_reservation.application.concert.dto;

import com.server.concert_reservation.domain.concert.dto.ReservationOutboxInfo;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;

import java.time.LocalDateTime;

public record ReservationOutboxResult(String messageId,
                                      String kafkaMessageId,
                                      OutboxStatus status,
                                      String payload,
                                      int retryCount,
                                      LocalDateTime retryAt,
                                      LocalDateTime createdAt,
                                      LocalDateTime updatedAt
) {

    public static ReservationOutboxResult from(ReservationOutboxInfo reservationOutboxInfo) {
        return new ReservationOutboxResult(reservationOutboxInfo.messageId(),
                reservationOutboxInfo.kafkaMessageId(),
                reservationOutboxInfo.status(),
                reservationOutboxInfo.payload(),
                reservationOutboxInfo.retryCount(),
                reservationOutboxInfo.retryAt(),
                reservationOutboxInfo.createdAt(),
                reservationOutboxInfo.updatedAt());
    }
}
