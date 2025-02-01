package com.server.concert_reservation.application.user.dto;

import com.server.concert_reservation.domain.user.dto.WalletInfo;

import java.time.LocalDateTime;

public record WalletResult(Long id,
                           Long userId,
                           int amount,
                           LocalDateTime createAt,
                           LocalDateTime updatedAt
) {

    public static WalletResult from(WalletInfo wallet) {
        return new WalletResult(wallet.id(),
                wallet.userId(),
                wallet.amount(),
                wallet.createAt(),
                wallet.updatedAt());
    }
}
