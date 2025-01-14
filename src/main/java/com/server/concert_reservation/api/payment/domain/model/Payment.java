package com.server.concert_reservation.api.payment.domain.model;

import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.infrastructure.entity.PaymentEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    private Long id;
    private Long userId;
    private Long reservationId;
    private int amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment of(Long id,
                             Long userId,
                             Long reservationId,
                             int amount,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        return new Payment(id, userId, reservationId, amount, createdAt, updatedAt);
    }

    public static Payment create(PaymentCommand command, int totalPrice) {
        return Payment.builder()
                .userId(command.userId())
                .reservationId(command.reservationId())
                .amount(totalPrice)
                .build();
    }

    public PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .build();
    }
}
