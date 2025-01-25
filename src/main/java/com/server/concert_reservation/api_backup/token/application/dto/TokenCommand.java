package com.server.concert_reservation.api_backup.token.application.dto;

import com.server.concert_reservation.interfaces.api.queue_token.dto.QueueTokenHttp;


public record TokenCommand(Long userId, String token) {
    public static TokenCommand of(QueueTokenHttp.QueueTokenRequest request) {
        return new TokenCommand(request.userId(), request.token());
    }
}
