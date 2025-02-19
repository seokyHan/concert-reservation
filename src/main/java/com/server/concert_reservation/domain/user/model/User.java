package com.server.concert_reservation.domain.user.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User of(Long id,
                          String name,
                          LocalDateTime createdAt,
                          LocalDateTime updatedAt) {
        return new User(id, name, createdAt, updatedAt);
    }


}
