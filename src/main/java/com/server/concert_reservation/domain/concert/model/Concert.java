package com.server.concert_reservation.domain.concert.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Concert {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Concert of(Long id, String title, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Concert(id, title, description, createdAt, updatedAt);
    }

}
