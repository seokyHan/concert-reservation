package com.server.concert_reservation.infrastructure.redis.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class WaitingQueueCoreReader implements WaitingQueueReader {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @Override
    public Long findRankInWaitingQueue(String uuid) {
        return redisTemplate.opsForZSet().rank(waitingQueueKey, uuid);
    }

    @Override
    public Long findRankInActiveQueue(String uuid) {
        return redisTemplate.opsForZSet().rank(activeQueueKey, uuid);
    }

    @Override
    public Double findScoreInActiveQueue(String uuid) {
        return redisTemplate.opsForZSet().score(activeQueueKey, uuid);
    }

    @Override
    public Set<Object> getWaitingQueue(int count) {
        return redisTemplate.opsForZSet().range(waitingQueueKey, 0, count - 1);
    }

    @Override
    public Set<Object> findAllActiveTokens() {
        return redisTemplate.opsForZSet().range(activeQueueKey, 0, -1);
    }
}
