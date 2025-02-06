package com.server.concert_reservation.infrastructure.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WaitingQueueWriterIntegrationTest {

    @Autowired
    private WaitingQueueWriter waitingQueueWriter;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @BeforeEach
    void tearDown() {
        redisTemplate.keys("*").forEach(redisTemplate::delete);
    }

    @Test
    @DisplayName("대기열 토큰을 생성한다.")
    void shouldAddWaitingQueue() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when
        String result = waitingQueueWriter.saveWaitingQueue(uuid);

        // then
        assertThat(result).isEqualTo(uuid);
    }

    @DisplayName("대기열 토큰을 활성화 한다.")
    @Test
    void shouldActivateWaitingQueues() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, 1);

        // when
        waitingQueueWriter.activateWaitingQueues(10, 10, TimeUnit.MINUTES);
        Long waitingQueueSize = redisTemplate.opsForZSet().size(waitingQueueKey);
        Double score = redisTemplate.opsForZSet().score(activeQueueKey, uuid);

        // then
        assertThat(waitingQueueSize).isZero();
        assertThat(score).isNotNull();
    }

    @Test
    @DisplayName("입력받은 토큰으로 활성화 토큰을 삭제한다.")
    void shouldDeleteActiveTokenByUuid() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, 1);

        // when
        waitingQueueWriter.deleteActiveTokenByUuid(uuid);
        Long result = redisTemplate.opsForZSet().size(activeQueueKey);

        // then
        assertThat(result).isZero();
    }

    @DisplayName("활성화 토큰을 삭제한다.")
    @Test
    void shouldDeleteActiveToken() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, 1);

        // when
        waitingQueueWriter.activateWaitingQueues(1, 1, TimeUnit.MILLISECONDS);
        Long result = redisTemplate.opsForZSet().size(waitingQueueKey);
        
        // then
        assertThat(result).isZero();

    }
}
