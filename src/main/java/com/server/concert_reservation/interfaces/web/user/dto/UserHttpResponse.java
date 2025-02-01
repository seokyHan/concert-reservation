package com.server.concert_reservation.interfaces.web.user.dto;

import com.server.concert_reservation.application.user.dto.WalletResult;

import java.time.LocalDateTime;

public class UserHttpResponse {

    public record UserWalletResponse(Long id,
                                     Long userId,
                                     int amount,
                                     LocalDateTime createAt,
                                     LocalDateTime updatedAt
    ) {
        public static UserWalletResponse of(WalletResult walletResult) {
            return new UserWalletResponse(walletResult.id(), walletResult.userId(), walletResult.amount(), walletResult.createAt(), walletResult.updatedAt());
        }
    }
}
