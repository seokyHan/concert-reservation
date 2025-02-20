package com.server.concert_reservation.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, String key, Object payload) {
        try {
            val jsonPayload = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, key, jsonPayload);
            log.info("send message to topic : {}, key : {}, payload : {}", topic, key, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to send Message - serialize message failed", e);
        }
    }

}
