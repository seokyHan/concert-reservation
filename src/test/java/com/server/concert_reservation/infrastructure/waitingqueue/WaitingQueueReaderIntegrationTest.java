package com.server.concert_reservation.infrastructure.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WaitingQueueReaderIntegrationTest {

    @Autowired
    private WaitingQueueReader waitingQueueReader;
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
    @DisplayName("대기열에 UUID를 추가하면 순위를 정상적으로 조회할 수 있다.")
    void shouldFindRankInWaitingQueueTest() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid, System.currentTimeMillis());

        // when
        Long rank = waitingQueueReader.findRankInWaitingQueue(uuid);

        // then
        assertThat(rank).isNotNull().isEqualTo(0L);
    }

    @Test
    @DisplayName("활성열에 UUID를 추가하면 순위를 정상적으로 조회할 수 있다.")
    void shouldFindRankInActiveQueueTest() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, System.currentTimeMillis());

        // when
        Long rank = waitingQueueReader.findRankInActiveQueue(uuid);

        // then
        assertThat(rank).isNotNull().isEqualTo(0L);
    }

    @DisplayName("활성열에서 특정 UUID의 점수를 조회할 수 있다.")
    @Test
    void shouldFindScoreInActiveQueueTest() {
        // given
        String uuid = UUID.randomUUID().toString();
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, score);

        // when
        Double retrievedScore = waitingQueueReader.findScoreInActiveQueue(uuid);

        // then
        assertThat(retrievedScore).isNotNull().isEqualTo(score);
    }

    @DisplayName("대기열에서 지정된 개수만큼 UUID를 조회할 수 있다.")
    @Test
    void shouldGetWaitingQueueTest() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid1, System.currentTimeMillis());
        redisTemplate.opsForZSet().add(waitingQueueKey, uuid2, System.currentTimeMillis() + 1);

        // when
        Set<Object> waitingQueue = waitingQueueReader.findWaitingQueue(1);

        // then
        assertThat(waitingQueue).isNotNull().hasSize(1);
    }

    @DisplayName("활성열에 있는 모든 UUID를 조회할 수 있다.")
    @Test
    void shouldGetIndAllActiveTokensTest() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid1, System.currentTimeMillis());
        redisTemplate.opsForZSet().add(activeQueueKey, uuid2, System.currentTimeMillis() + 1);

        // when
        Set<Object> activeTokens = waitingQueueReader.findAllActiveTokens();

        // then
        assertThat(activeTokens).isNotNull().hasSize(2);
        assertThat(activeTokens).contains(uuid1, uuid2);
    }
}
