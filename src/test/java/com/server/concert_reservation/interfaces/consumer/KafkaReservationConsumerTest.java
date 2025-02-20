package com.server.concert_reservation.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import com.server.concert_reservation.infrastructure.db.concert.repository.ReservationOutboxJpaRepository;
import com.server.concert_reservation.infrastructure.kafka.KafkaProducer;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class KafkaReservationConsumerTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private KafkaReservationConsumer kafkaReservationConsumer;

    @Autowired
    private ReservationOutboxJpaRepository reservationOutboxJpaRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void tearDown() {
        databaseCleanUp.execute();
    }


    @DisplayName("카프카 메세지 발행시 예약 아웃박스에 존재하는 이벤트의 상태가 PUBLISHED로 변경된다")
    @Test
    void shouldKafkaProduceMessageUpdateStatusPublished() throws Exception {
        //given
        ReservationEvent event = new ReservationEvent(1L);
        ReservationOutbox reservationOutbox = ReservationOutbox.builder()
                .messageId("test-uuid")
                .kafkaMessageId(event.reservationId().toString())
                .status(OutboxStatus.INIT)
                .payload(objectMapper.writeValueAsString(event))
                .retryCount(0)
                .build();
        reservationOutboxJpaRepository.save(reservationOutbox.toEntity(reservationOutbox));

        // when
        kafkaProducer.send("concert-reservation", event.reservationId().toString(), event);
        Thread.sleep(1000);

        // then
        Optional<ReservationOutbox> outboxEvent = reservationOutboxJpaRepository.findByKafkaMessageId(event.reservationId().toString());

        assertAll(
                () -> assertTrue(outboxEvent.isPresent()),
                () -> assertEquals(event.reservationId().toString(), outboxEvent.get().getKafkaMessageId()),
                () -> assertEquals(OutboxStatus.PUBLISHED, outboxEvent.get().getStatus())
        );

    }

}
