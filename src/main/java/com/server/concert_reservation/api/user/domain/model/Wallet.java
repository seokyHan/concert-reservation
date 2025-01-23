package com.server.concert_reservation.api.user.domain.model;

import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.user.domain.errorcode.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.api.user.domain.errorcode.UserErrorCode.NOT_ENOUGH_POINT;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wallet {

    private Long id;
    private Long userId;
    private int amount;
    private Long version;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public static Wallet of(Long id, Long userId, int amount, Long version, LocalDateTime createAt, LocalDateTime updatedAt) {
        return new Wallet(id, userId, amount, version, createAt, updatedAt);
    }

    public WalletEntity toEntity(Wallet wallet) {
        return WalletEntity.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .amount(wallet.getAmount())
                .version(wallet.getVersion())
                .build();
    }

    public void chargeAmount(int point) {
        if (point <= 0) {
            throw new CustomException(INVALID_POINT);
        }

        this.amount += point;
    }

    public void useAmount(int point) {
        if (isAmountLessThan(point)) {
            throw new CustomException(NOT_ENOUGH_POINT);
        }
        this.amount -= point;

    }

    public boolean isAmountLessThan(int point) {
        return this.amount < point;
    }

}
