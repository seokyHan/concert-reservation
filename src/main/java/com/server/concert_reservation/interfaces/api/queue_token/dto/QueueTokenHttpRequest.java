package com.server.concert_reservation.interfaces.api.queue_token.dto;

public class QueueTokenHttpRequest {

    public record QueueTokenRequest(Long userId,
                                    String token
    ) {
    }
}
