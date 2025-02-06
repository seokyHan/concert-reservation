package com.server.concert_reservation.infrastructure.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @DisplayName("대기열의 순번을 조회한다.")
    @Test
    void shouldGetWaitingQueuePosition() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet()
                .add(waitingQueueKey, uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        // when
        Long result = waitingQueueReader.findWaitingQueuePosition(uuid);

        // then
        assertThat(result).isOne();
    }

    @DisplayName("활성화 토큰을 조회 한다.")
    @Test
    void shouldGetActiveToken() {
        // given
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, 1);

        // when
        WaitingQueueInfo result = waitingQueueReader.findActiveToken(uuid);

        // then
        assertThat(result.uuid()).isEqualTo(uuid);
    }

    @DisplayName("활성화 된 토큰 목록을 조회한다.")
    @Test
    void shouldGetActiveTokens() {
        // given
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        redisTemplate.opsForZSet().add(activeQueueKey, uuid1, 1);
        redisTemplate.opsForZSet().add(activeQueueKey, uuid2, 2);

        // when
        List<WaitingQueueInfo> result = waitingQueueReader.findAllActiveTokens();

        // then
        assertEquals(2, result.size());
    }
}
