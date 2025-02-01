package com.server.concert_reservation.application.queue_token.dto;

import com.server.concert_reservation.interfaces.web.queue_token.dto.QueueTokenHttpRequest;


public record QueueTokenCommand(Long userId) {
    public static QueueTokenCommand of(QueueTokenHttpRequest.QueueTokenRequest request) {
        return new QueueTokenCommand(request.userId());
    }
}
