package com.server.concert_reservation.domain.user.model;

import com.server.concert_reservation.infrastructure.user.entity.WalletEntity;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.NOT_ENOUGH_POINT;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wallet {

    private Long id;
    private Long userId;
    private int amount;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public static Wallet of(Long id, Long userId, int amount, LocalDateTime createAt, LocalDateTime updatedAt) {
        return new Wallet(id, userId, amount, createAt, updatedAt);
    }

    public WalletEntity toEntity(Wallet wallet) {
        return WalletEntity.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .amount(wallet.getAmount())
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
