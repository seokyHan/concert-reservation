package com.server.concert_reservation.infrastructure.spring;

import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import com.server.concert_reservation.domain.concert.event.ReservationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSpringEventPublisher implements ReservationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(ReservationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
