package com.server.concert_reservation.domain.waitingqueue.dto;

import com.server.concert_reservation.infrastructure.redis.waitingqueue.WaitingQueue;

import java.time.LocalDateTime;

public record WaitingQueueInfo(
        String uuid,
        LocalDateTime expiredAt
) {

    public static WaitingQueueInfo of(WaitingQueue waitingQueue) {
        return new WaitingQueueInfo(waitingQueue.getUuid(), waitingQueue.getExpiredAt());
    }

}
