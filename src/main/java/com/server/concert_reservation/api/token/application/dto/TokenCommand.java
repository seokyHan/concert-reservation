package com.server.concert_reservation.api.token.application.dto;

import com.server.concert_reservation.api.token.interfaces.dto.TokenHttp;


public record TokenCommand(Long userId, String token) {
    public static TokenCommand of(TokenHttp.TokenRequest request) {
        return new TokenCommand(request.userId(), request.token());
    }
}
