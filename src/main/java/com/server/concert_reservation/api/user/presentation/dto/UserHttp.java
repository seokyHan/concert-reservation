package com.server.concert_reservation.api.user.presentation.dto;

import com.server.concert_reservation.api.user.domain.dto.WalletInfo;

import java.time.LocalDateTime;

public class UserHttp {

    public record UserWalletRequest(Long userId, int point) {}
    public record UserWalletResponse(Long id,
                                     Long userId,
                                     int amount,
                                     LocalDateTime createAt,
                                     LocalDateTime updatedAt
    ) {
        public static UserWalletResponse of (WalletInfo walletInfo) {
            return new UserWalletResponse(walletInfo.id(), walletInfo.userId(), walletInfo.amount(), walletInfo.createAt(), walletInfo.updatedAt());
        }
    }

}
