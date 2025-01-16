package com.server.concert_reservation.api.token.interfaces.scheduler;

import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.token.application.TokenQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final TokenCommandUseCase tokenCommandUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;

    @Scheduled(fixedDelayString = "60000")
    public void activeWaitingToken() {
        log.info("대기열 토큰 활성화 스케줄러 실행");
        val waitingTokens= tokenQueryUseCase.getWaitingToken(100);
        waitingTokens.forEach(waitingToken -> {
            try {
                tokenCommandUseCase.activateToken(waitingToken.getToken());
            } catch (Exception e) {
                log.warn("대기열 토큰 활성화 스케쥴러 실행 중 오류 발생 [Token: {}]: {}", waitingToken.getToken(), e.getMessage());
            }
        });
    }


    @Scheduled(fixedDelayString = "60000")
    public void expireWaitingToken() {
        log.info("대기열 토큰 만료 스케줄러 실행");
        // 대기열 토큰 활성화 시간(activeAt) 10분
        // activeAt이 10분이 지났을 경우 만료 처리 후 expireAt에 업데이트
        val waitingTokens = tokenQueryUseCase.getWaitingTokensToBeExpired(10);
        waitingTokens.forEach(waitingToken -> {
            try {
                tokenCommandUseCase.expireToken(waitingToken.getToken());
            } catch (Exception e) {
                log.warn("대기열 토큰 만료 스케쥴러 실행 중 오류 발생 [Token: {}]: {}", waitingToken.getToken(), e.getMessage());
            }
        });
    }

}