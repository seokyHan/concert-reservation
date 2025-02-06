package com.server.concert_reservation.interfaces.web.waitingqueue.dto;

import com.server.concert_reservation.application.waitingqueue.dto.WaitingQueueWithPositionResult;

public class WaitingQueueHttpResponse {

    public record WaitingQueueResponse(String uuid) {
        public static WaitingQueueResponse from(String uuid) {
            return new WaitingQueueResponse(uuid);
        }
    }

    public record WaitingQueuePositionResponse(WaitingQueueWithPositionResult waitingQueueWithPositionResult) {
        public static WaitingQueuePositionResponse from(WaitingQueueWithPositionResult waitingQueueWithPositionResult) {
            return new WaitingQueuePositionResponse(waitingQueueWithPositionResult);
        }
    }
}
