package com.server.concert_reservation.infrastructure.payment.entity;

import com.server.concert_reservation.domain.payment.model.Payment;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class PaymentEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "reservation_id")
    private Long reservationId;
    @Column(name = "amount")
    private int amount;

    @Builder
    public PaymentEntity(Long id, Long userId, Long reservationId, int amount) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.amount = amount;
    }

    public Payment toDomain() {
        return Payment.of(id, userId, reservationId, amount, createdAt, updatedAt);
    }


}
