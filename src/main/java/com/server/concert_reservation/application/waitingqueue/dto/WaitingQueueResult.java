package com.server.concert_reservation.application.waitingqueue.dto;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;

import java.time.LocalDateTime;


public record WaitingQueueResult(String uuid, LocalDateTime expiredAt) {

    public static WaitingQueueResult from(WaitingQueueInfo waitingQueueInfo) {
        return new WaitingQueueResult(
                waitingQueueInfo.uuid(),
                waitingQueueInfo.expiredAt()
        );
    }
}
