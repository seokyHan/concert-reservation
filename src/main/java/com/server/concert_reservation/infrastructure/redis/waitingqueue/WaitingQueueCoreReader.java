package com.server.concert_reservation.infrastructure.redis.waitingqueue;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.ACTIVE_QUEUE_NOT_FOUND;
import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.WAITING_QUEUE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class WaitingQueueCoreReader implements WaitingQueueReader {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${queue.waiting-key}")
    private String waitingQueueKey;
    @Value("${queue.active-key}")
    private String activeQueueKey;

    @Override
    public Long findWaitingQueuePosition(String uuid) {
        val rank = redisTemplate.opsForZSet().rank(waitingQueueKey, uuid);
        if (rank == null) {
            if (redisTemplate.opsForZSet().rank(activeQueueKey, uuid) != null) {
                return 0L;
            }
            throw new CustomException(WAITING_QUEUE_NOT_FOUND);
        }

        return rank + 1;
    }

    @Override
    public WaitingQueueInfo findActiveToken(String uuid) {
        val score = redisTemplate.opsForZSet().score(activeQueueKey, uuid);
        if (score == null) {
            throw new CustomException(ACTIVE_QUEUE_NOT_FOUND);
        }

        return WaitingQueueInfo.of(
                WaitingQueue.builder()
                        .uuid(uuid)
                        .expiredAt(LocalDateTime.ofEpochSecond(score.longValue(), 0, ZoneOffset.UTC))
                        .build()
        );
    }

    @Override
    public List<WaitingQueueInfo> findAllActiveTokens() {
        val activeTokens = redisTemplate.opsForZSet()
                .range(activeQueueKey, 0, -1)
                .stream()
                .map(uuid -> WaitingQueue.builder()
                        .uuid(uuid.toString())
                        .expiredAt(LocalDateTime.ofEpochSecond(
                                redisTemplate.opsForZSet().score(activeQueueKey, uuid).longValue(), 0,
                                ZoneOffset.UTC))
                        .build())
                .toList();

        return activeTokens.stream()
                .map(WaitingQueueInfo::of)
                .collect(Collectors.toList());
    }
}
