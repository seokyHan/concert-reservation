package com.server.concert_reservation.application.user.dto;

import com.server.concert_reservation.domain.user.dto.UserInfo;

public record UserResult() {

    public static UserResult from(UserInfo user) {
        return new UserResult();
    }
}
