package com.server.concert_reservation.domain.concert.event;

import com.server.concert_reservation.domain.concert.event.dto.ReservationSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(ReservationSuccessEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
