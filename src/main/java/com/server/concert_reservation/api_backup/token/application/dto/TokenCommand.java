package com.server.concert_reservation.api_backup.token.application.dto;

import com.server.concert_reservation.interfaces.web.queue_token.dto.QueueTokenHttpRequest;


public record TokenCommand(Long userId, String token) {
    public static TokenCommand of(QueueTokenHttpRequest.QueueTokenRequest request) {
        return new TokenCommand(request.userId(), request.token());
    }
}
