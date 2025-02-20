package com.server.concert_reservation.infrastructure.kafka;

import com.server.concert_reservation.domain.concert.event.ReservationEvent;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicInteger receivedMessageCount = new AtomicInteger(0);

    @DisplayName("카프카 메시지 발행 및 수신 테스트")
    @Test
    void kafkaMessageProduceAndConsumeTest() {
        int messageCount = 10;
        ReservationEvent reservationEvent = Instancio.create(ReservationEvent.class);

        for (int i = 0; i < messageCount; i++) {
            kafkaProducer.send("test-topic", "Hello, Kafka! " + i, reservationEvent);
        }

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(receivedMessageCount.get()).isEqualTo(messageCount));

    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consumeTestMessage(String message) {
        logger.info("메세지 수신 : {}", message);
        receivedMessageCount.incrementAndGet();
    }
}
