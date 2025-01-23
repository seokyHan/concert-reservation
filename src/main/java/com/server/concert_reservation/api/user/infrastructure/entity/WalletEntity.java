package com.server.concert_reservation.api.user.infrastructure.entity;


import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
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
    @Version
    private Long version;

    @Builder
    public WalletEntity(Long id, Long userId, int amount, Long version) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.version = version;
    }

    public Wallet toDomain() {
        return Wallet.of(id, userId, amount, version, createdAt, updatedAt);
    }
}
