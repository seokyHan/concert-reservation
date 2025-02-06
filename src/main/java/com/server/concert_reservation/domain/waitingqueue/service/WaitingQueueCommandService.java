package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class WaitingQueueCommandService {

    private final WaitingQueueWriter waitingQueueWriter;
    private final UUIDManager uuidManager;

    public String createWaitingToken() {
        return waitingQueueWriter.saveWaitingQueue(uuidManager.generateUuid());
    }

    public void activateWaitingQueue(int availableSlots, int timeout) {
        waitingQueueWriter.activateWaitingQueues(availableSlots, timeout, TimeUnit.MINUTES);
    }

    public void removeActiveQueueByUuid(String uuid) {
        waitingQueueWriter.deleteActiveTokenByUuid(uuid);
    }

    public void removeActiveQueue() {
        waitingQueueWriter.deleteActiveToken();
    }

}
