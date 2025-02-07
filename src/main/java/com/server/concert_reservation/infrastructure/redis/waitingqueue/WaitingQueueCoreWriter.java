package com.server.concert_reservation.infrastructure.redis.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingQueueCoreWriter implements WaitingQueueWriter {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @Override
    public boolean addWaitingQueue(String uuid, Long score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(waitingQueueKey, uuid, score));
    }

    @Override
    public void moveToActiveQueue(String uuid, Long expirationTimestamp) {
        redisTemplate.opsForZSet().add(activeQueueKey, uuid, expirationTimestamp);
        redisTemplate.opsForZSet().remove(waitingQueueKey, uuid);
    }

    @Override
    public void removeActiveTokenByUuid(String uuid) {
        redisTemplate.opsForZSet().remove(activeQueueKey, uuid);
    }

    @Override
    public void removeExpiredTokens(Long currentTimestamp) {
        redisTemplate.opsForZSet().removeRangeByScore(activeQueueKey, 0, currentTimestamp);
    }

}
