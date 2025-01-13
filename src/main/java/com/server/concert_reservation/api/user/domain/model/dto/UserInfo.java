package com.server.concert_reservation.api.user.domain.model.dto;

import com.server.concert_reservation.api.user.domain.model.User;

import java.time.LocalDateTime;

public record UserInfo(Long id,
                       String name,
                       LocalDateTime createAt,
                       LocalDateTime updatedAt) {

    public static UserInfo from (User user){
        return new UserInfo(user.getId(), user.getName(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
