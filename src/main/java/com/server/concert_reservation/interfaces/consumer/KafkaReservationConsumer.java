package com.server.concert_reservation.interfaces.consumer;

import com.server.concert_reservation.application.concert.ConcertUseCase;
import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import com.server.concert_reservation.infrastructure.external.DataPlatformSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaReservationConsumer {

    private final ConcertUseCase concertUseCase;
    private final DataPlatformSendService dataPlatformSendService;

    @KafkaListener(groupId = "outbox-group", topics = "concert-reservation")
    public void consumeOutbox(ConsumerRecord<String, Object> data, Acknowledgment ack) {
        log.info("message consume11 : {}", data.topic());
        concertUseCase.publishReservationOutbox(data.key());
        ack.acknowledge();

    }

    @KafkaListener(groupId = "external-platform-group", topics = "external-platform")
    public void consumeExternalPlatform(ReservationEvent event, Acknowledgment ack) {
        dataPlatformSendService.send(event);
        ack.acknowledge();
    }
}
