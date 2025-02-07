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
    @DisplayName("대기열에 UUID를 추가할 수 있다.")
    void testAddWaitingQueue() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long score = System.currentTimeMillis();

        // when
        boolean result = waitingQueueWriter.addWaitingQueue(uuid, score);

        // then
        assertThat(result).isTrue();
        assertThat(redisTemplate.opsForZSet().rank(waitingQueueKey, uuid)).isNotNull();
    }

    @Test
    @DisplayName("대기열의 UUID를 활성열로 이동할 수 있다.")
    void testMoveToActiveQueue() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long score = System.currentTimeMillis();
        Long expirationTimestamp = score + 60000;
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, score);

        // when
        waitingQueueWriter.moveToActiveQueue(uuid, expirationTimestamp);

        // then
        assertThat(redisTemplate.opsForZSet().rank(waitingQueueKey, uuid)).isNull();
        assertThat(redisTemplate.opsForZSet().rank(activeQueueKey, uuid)).isNotNull();
    }

    @Test
    @DisplayName("활성열에서 특정 UUID를 제거할 수 있다.")
    void testRemoveActiveTokenByUuid() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long expirationTimestamp = System.currentTimeMillis() + 60000;
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, expirationTimestamp);

        // when
        waitingQueueWriter.removeActiveTokenByUuid(uuid);

        // then
        assertThat(redisTemplate.opsForZSet().rank(activeQueueKey, uuid)).isNull();
    }

    @Test
    @DisplayName("활성열에서 만료된 토큰을 제거할 수 있다.")
    void testRemoveExpiredTokens() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid1, now - 10000); // 만료됨
        redisTemplate.opsForZSet().add(activeQueueKey, uuid2, now + 10000); // 유효함

        // when
        waitingQueueWriter.removeExpiredTokens(now);

        // then
        assertThat(redisTemplate.opsForZSet().rank(activeQueueKey, uuid1)).isNull();
        assertThat(redisTemplate.opsForZSet().rank(activeQueueKey, uuid2)).isNotNull();
    }
}
