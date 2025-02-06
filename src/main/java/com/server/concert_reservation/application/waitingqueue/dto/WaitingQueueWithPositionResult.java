package com.server.concert_reservation.application.waitingqueue.dto;

public record WaitingQueueWithPositionResult(String uuid, Long position) {

    public static WaitingQueueWithPositionResult of(String uuid, Long position) {
        return new WaitingQueueWithPositionResult(uuid, position);
    }
}
