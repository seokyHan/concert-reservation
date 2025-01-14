package com.server.concert_reservation.api.user.application.dto;

import com.server.concert_reservation.api.user.domain.model.Wallet;

import java.time.LocalDateTime;

public record WalletInfo(Long id,
                         Long userId,
                         int amount,
                         LocalDateTime createAt,
                         LocalDateTime updatedAt
) {

    public static WalletInfo from (Wallet wallet){
        return new WalletInfo(wallet.getId(), wallet.getUserId(), wallet.getAmount(), wallet.getCreateAt(), wallet.getUpdatedAt());
    }
}
