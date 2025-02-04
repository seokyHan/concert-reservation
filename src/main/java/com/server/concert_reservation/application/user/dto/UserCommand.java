package com.server.concert_reservation.application.user.dto;

import com.server.concert_reservation.interfaces.web.user.dto.UserHttpRequest;

public record UserCommand(Long userId, int point) {

    public static UserCommand from(UserHttpRequest.UserWalletRequest request) {
        return new UserCommand(request.userId(), request.point());
    }

    public static UserCommand of(Long userId, int point) {
        return new UserCommand(userId, point);
    }
}
