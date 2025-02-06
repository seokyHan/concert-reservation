package com.server.concert_reservation.infrastructure.redis.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class WaitingQueueCoreWriter implements WaitingQueueWriter {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @Override
    public String saveWaitingQueue(String uuid) {
        return redisTemplate.opsForZSet()
                .add(waitingQueueKey, uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) ?
                uuid :
                null;
    }

    @Override
    public void activateWaitingQueues(int availableSlots, long timeout, TimeUnit unit) {
        redisTemplate.opsForZSet()
                .range(waitingQueueKey, 0, availableSlots - 1)
                .forEach(uuid -> {
                    val expirationTimestamp = LocalDateTime.now()
                            .plus(timeout, unit.toChronoUnit())
                            .toEpochSecond(ZoneOffset.UTC);

                    redisTemplate.opsForZSet().add(activeQueueKey, uuid, expirationTimestamp);
                    redisTemplate.opsForZSet().remove(waitingQueueKey, uuid);
                });
    }

    @Override
    public void deleteActiveTokenByUuid(String uuid) {
        redisTemplate.opsForZSet().remove(activeQueueKey, uuid);
    }

    @Override
    public void deleteActiveToken() {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        redisTemplate.opsForZSet().removeRangeByScore(activeQueueKey, 0, now);
    }

}
