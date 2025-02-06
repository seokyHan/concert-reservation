package com.server.concert_reservation.infrastructure.db.user.entity;


import com.server.concert_reservation.infrastructure.db.auditing.BaseTimeEntity;
import com.server.concert_reservation.domain.user.model.Wallet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallet")
public class WalletEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "amount")
    private int amount;

    @Builder
    public WalletEntity(Long id, Long userId, int amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public Wallet toDomain() {
        return Wallet.of(id, userId, amount, createdAt, updatedAt);
    }
}
