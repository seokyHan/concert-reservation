package com.server.concert_reservation.api_backup.user.application.dto;

import com.server.concert_reservation.domain.user.model.User;

import java.time.LocalDateTime;

public record UserInfo(Long id,
                       String name,
                       LocalDateTime createAt,
                       LocalDateTime updatedAt) {

    public static UserInfo from(User user) {
        return new UserInfo(user.getId(), user.getName(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
