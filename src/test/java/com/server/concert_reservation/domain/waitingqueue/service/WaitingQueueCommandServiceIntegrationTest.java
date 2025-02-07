package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Transactional
class WaitingQueueCommandServiceIntegrationTest {

    @Autowired
    private WaitingQueueCommandService waitingQueueCommandService;
    @Autowired
    private WaitingQueueWriter waitingQueueWriter;
    @Autowired
    private WaitingQueueReader waitingQueueReader;
    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeEach
    void redisCleansing() {
        redisTemplate.keys("*").forEach(redisTemplate::delete);
    }


    @DisplayName("대기 토큰을 생성 성공")
    @Test
    void shouldSuccessfullyCreateWaitingQueue() {
        String result = waitingQueueCommandService.createWaitingToken();

        // then
        assertNotNull(result);
    }

    @Test
    @DisplayName("대기 토큰 활성화를 성공한다.")
    void shouldSuccessfullyActivateWaitingQueues() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        waitingQueueWriter.addWaitingQueue(uuid1, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        waitingQueueWriter.addWaitingQueue(uuid2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        // when
        waitingQueueCommandService.activateWaitingQueue(2, 10);

        // then
        Set<Object> activeTokens = waitingQueueReader.findAllActiveTokens();
        assertThat(activeTokens).hasSize(2);
    }

}