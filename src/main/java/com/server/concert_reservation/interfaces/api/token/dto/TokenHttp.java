package com.server.concert_reservation.interfaces.api.token.dto;

import java.time.LocalDateTime;

public class TokenHttp {

    public record TokenRequest(Long userId) {}

    public record TokenResponse(WaitingToken waitingToken) {
        public record WaitingToken(Long id,
                                   String uuid,
                                   Long userId,
                                   String status,
                                   LocalDateTime expiredAt) {

        }
    }
}
