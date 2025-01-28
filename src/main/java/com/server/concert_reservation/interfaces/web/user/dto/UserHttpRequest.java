package com.server.concert_reservation.interfaces.web.user.dto;

public class UserHttpRequest {

    public record UserWalletRequest(Long userId,
                                    int point
    ) {
    }
}
