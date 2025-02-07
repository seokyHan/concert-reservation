package com.server.concert_reservation.domain.waitingqueue.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class WaitingQueue {

    private String uuid;
    private LocalDateTime expiredAt;

    @Builder
    public WaitingQueue(String uuid, LocalDateTime expiredAt) {
        this.uuid = uuid;
        this.expiredAt = expiredAt;
    }
}
