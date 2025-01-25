package com.server.concert_reservation.interfaces.api.user.dto;

import com.server.concert_reservation.api_backup.user.application.dto.WalletInfo;

import java.time.LocalDateTime;

public class UserHttpResponse {

    public record UserWalletResponse(Long id,
                                     Long userId,
                                     int amount,
                                     LocalDateTime createAt,
                                     LocalDateTime updatedAt
    ) {
        public static UserWalletResponse of(WalletInfo walletInfo) {
            return new UserWalletResponse(walletInfo.id(), walletInfo.userId(), walletInfo.amount(), walletInfo.createAt(), walletInfo.updatedAt());
        }
    }
}
