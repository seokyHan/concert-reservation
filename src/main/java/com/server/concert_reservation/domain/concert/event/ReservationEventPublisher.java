package com.server.concert_reservation.domain.concert.event;

public interface ReservationEventPublisher {
    void publish(ReservationEvent event);
}
