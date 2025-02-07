package com.server.concert_reservation.application.waitingqueue;

import com.server.concert_reservation.application.waitingqueue.dto.WaitingQueueWithPositionResult;
import com.server.concert_reservation.domain.waitingqueue.service.WaitingQueueCommandService;
import com.server.concert_reservation.domain.waitingqueue.service.WaitingQueueQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaitingQueueUseCase {

    private final WaitingQueueCommandService waitingQueueCommandService;
    private final WaitingQueueQueryService waitingQueueQueryService;

    @Transactional
    public String issueWaitingQueueToken() {
        return waitingQueueCommandService.createWaitingToken();
    }

    public WaitingQueueWithPositionResult getWaitingQueuePosition(String uuid) {
        val waitingQueuePosition = waitingQueueQueryService.getWaitingQueuePosition(uuid);
        return WaitingQueueWithPositionResult.of(waitingQueuePosition.uuid(), waitingQueuePosition.position());
    }

    public void validateWaitingQueueProcessing(String uuid) {
        waitingQueueQueryService.validateWaitingQueueProcessing(uuid);
    }

    @Transactional
    public void activateWaitingQueues(int availableSlots, int timeout) {
        waitingQueueCommandService.activateWaitingQueue(availableSlots, timeout);
    }

    @Transactional
    public void removeActivateQueue() {
        waitingQueueCommandService.removeExpiredActiveTokens();
    }
}
