package com.server.concert_reservation.interfaces.api.user.dto;

public class UserHttp {

    public record UserBalanceRequest(Long userId, int amount) {}
    public record UserPaymentRequest(Long reservationId) {}
    public record UserBalanceResponse(Balance balance) {
        public record Balance(Long id, Long userId, int amount){ }
    }

    public record UserPaymentResponse(Payment payment) {
        public record Payment(Long id, Long reservationId, Long userId, int amount){ }
    }
}
