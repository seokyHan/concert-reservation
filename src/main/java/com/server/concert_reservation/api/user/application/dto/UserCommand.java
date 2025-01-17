package com.server.concert_reservation.api.user.application.dto;

import com.server.concert_reservation.api.user.interfaces.dto.UserHttp;

public record UserCommand(Long userId, int point) {

    public static UserCommand of(UserHttp.UserWalletRequest request) {
        return new UserCommand(request.userId(), request.point());
    }

    public static UserCommand from(Long userId, int point) {
        return new UserCommand(userId, point);
    }
}
