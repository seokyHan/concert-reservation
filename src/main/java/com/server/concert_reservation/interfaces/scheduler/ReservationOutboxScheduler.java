package com.server.concert_reservation.interfaces.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.concert_reservation.application.concert.ConcertSchedulerUseCase;
import com.server.concert_reservation.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationOutboxScheduler {

    private final ConcertSchedulerUseCase concertSchedulerUseCase;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 * * * * *")
    public void republishReservationMessage() {
        log.info("발행되지 않은 reservation message 재처리 스케쥴러 실행");
        val reservationOutboxes = concertSchedulerUseCase.getPendingReservationOutboxMessage(5);
        reservationOutboxes.forEach(outbox -> {
            try {
                val payload = objectMapper.readValue(outbox.payload(), Object.class);
                kafkaProducer.send("concert-reservation", outbox.kafkaMessageId(), payload);
            } catch (Exception e) {
                log.warn("reservation outbox 재발송 중 오류 발생 (ID: {}): {}", outbox.kafkaMessageId(), e.getMessage());
            }
        });
    }
}
