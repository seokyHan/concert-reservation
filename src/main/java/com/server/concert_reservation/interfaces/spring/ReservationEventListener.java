package com.server.concert_reservation.interfaces.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.concert_reservation.application.concert.ConcertUseCase;
import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import com.server.concert_reservation.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final ConcertUseCase concertUseCase;
    private final KafkaProducer kafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutboxEvent(ReservationEvent event) throws JsonProcessingException {
        concertUseCase.createReservationOutbox(event.reservationId().toString(), event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationEvent(ReservationEvent event) {
        kafkaProducer.send("concert-reservation", event.reservationId().toString(), event);
    }
}
