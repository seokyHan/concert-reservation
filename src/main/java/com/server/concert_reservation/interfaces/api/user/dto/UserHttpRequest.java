package com.server.concert_reservation.interfaces.api.user.dto;

public class UserHttpRequest {

    public record UserWalletRequest(Long userId,
                                    int point
    ) {
    }
}
