package com.server.concert_reservation.domain.waitingqueue.dto;

public record WaitingQueueWithPositionInfo(String uuid, Long position) {

    public static WaitingQueueWithPositionInfo of(String uuid, Long position) {
        return new WaitingQueueWithPositionInfo(uuid, position);
    }
}
