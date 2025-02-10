package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueWithPositionInfo;
import com.server.concert_reservation.domain.waitingqueue.model.WaitingQueue;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import com.server.concert_reservation.interfaces.web.support.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.*;

@Service
@RequiredArgsConstructor
public class WaitingQueueQueryService {

    private final WaitingQueueReader waitingQueueReader;
    private final TimeManager timeManager;

    public WaitingQueueWithPositionInfo getQueuePosition(String uuid) {
        val waitingQueueRank = waitingQueueReader.findRankInWaitingQueue(uuid);
        if (waitingQueueRank != null) {
            return WaitingQueueWithPositionInfo.of(uuid, waitingQueueRank + 1);
        }

        val activeQueueRank = waitingQueueReader.findRankInActiveQueue(uuid);
        if (activeQueueRank != null) {
            return WaitingQueueWithPositionInfo.of(uuid, 0L);
        }

        throw new CustomException(WAITING_QUEUE_NOT_FOUND);
    }

    public WaitingQueueInfo validateWaitingQueueProcessing(String uuid) {
        val score = waitingQueueReader.findScoreInActiveQueue(uuid);
        if (score == null) {
            throw new CustomException(ACTIVE_QUEUE_NOT_FOUND);
        }

        val expiredAt = LocalDateTime.ofEpochSecond(score.longValue(), 0, ZoneOffset.UTC);
        if (timeManager.now().isAfter(expiredAt)) {
            throw new CustomException(WAITING_QUEUE_EXPIRED);
        }

        return WaitingQueueInfo.of(createWaitingQueueBuilder(uuid, expiredAt));
    }

    public Set<Object> getPrimaryWaitingQueue(int availableSlots) {
        val waitingQueue = waitingQueueReader.findWaitingQueue(availableSlots);
        if (waitingQueue.isEmpty() || waitingQueue == null) {
            throw new CustomException(WAITING_QUEUE_NOT_FOUND);
        }

        return waitingQueue;
    }

    private WaitingQueue createWaitingQueueBuilder(String uuid, LocalDateTime expiredAt) {
        return WaitingQueue.builder()
                .uuid(uuid)
                .expiredAt(expiredAt)
                .build();
    }


}
