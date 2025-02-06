package com.server.concert_reservation.interfaces.scheduler;

import com.server.concert_reservation.application.waitingqueue.WaitingQueueUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final WaitingQueueUseCase waitingQueueUseCase;

    @Scheduled(cron = "0 * * * * *")
    public void activeWaitingToken() {
        log.info("대기열 토큰 활성화 스케줄러 실행");
        waitingQueueUseCase.activateWaitingQueues(10, 10);
    }


    @Scheduled(cron = "0 * * * * *")
    public void expireWaitingToken() {
        log.info("만료 토큰 삭제 스케줄러 실행");
        waitingQueueUseCase.removeActivateQueue();
    }

}