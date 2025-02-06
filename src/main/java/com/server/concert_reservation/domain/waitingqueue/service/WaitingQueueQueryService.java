package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueWithPositionInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.WAITING_QUEUE_EXPIRED;

@Service
@RequiredArgsConstructor
public class WaitingQueueQueryService {

    private final WaitingQueueReader waitingQueueReader;
    private final TimeManager timeManager;

    public WaitingQueueWithPositionInfo getWaitingQueuePosition(String uuid) {
        val position = waitingQueueReader.findWaitingQueuePosition(uuid);

        return WaitingQueueWithPositionInfo.of(uuid, position);
    }

    public WaitingQueueInfo validateWaitingQueueProcessing(String uuid) {
        val waitingQueue = waitingQueueReader.findActiveToken(uuid);
        if (timeManager.now().isAfter(waitingQueue.expiredAt())) {
            throw new CustomException(WAITING_QUEUE_EXPIRED);
        }
        return waitingQueue;
    }


}
