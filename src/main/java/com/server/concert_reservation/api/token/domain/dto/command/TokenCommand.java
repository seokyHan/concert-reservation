package com.server.concert_reservation.api.token.domain.dto.command;

import com.server.concert_reservation.api.token.presentation.dto.TokenHttp;


public record TokenCommand(Long userId, String token) {
    public static TokenCommand of(TokenHttp.TokenRequest request) {
        return new TokenCommand(request.userId(), request.token());
    }
}
