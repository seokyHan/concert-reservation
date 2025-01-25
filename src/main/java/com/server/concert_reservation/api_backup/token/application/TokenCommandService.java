package com.server.concert_reservation.api_backup.token.application;

import com.server.concert_reservation.api_backup.token.application.dto.TokenCommand;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class TokenCommandService implements TokenCommandUseCase {

    private final QueueTokenReader tokenReader;
    private final QueueTokenWriter tokenWriter;
    private final TimeManager timeManager;

    @Override
    public QueueToken createToken(TokenCommand command) {
        return tokenWriter.save(new QueueToken(command.userId(), command.token()));
    }

    /**
     * 현재 대기열이 활성화 상태인지 확인. 각 요청 전에 대기열 상태를 활성화 상태인지 확인할 때 사용
     */
    @Override
    public void checkActivatedToken(String token) {
        tokenReader.getByToken(token).validateToken();
    }

    /**
     * 대기열 토큰 활성화 처리 -> 스케줄러에서 호출
     */
    @Override
    public void activateToken(String token) {
        val currentWaitingToken = tokenReader.getByToken(token);
        currentWaitingToken.activate(timeManager.now());
        tokenWriter.save(currentWaitingToken);
    }

    /**
     * 활성화된 대기열 토큰 만료 처리 -> 스케줄러에서 주기적으로 호출
     */
    @Override
    public void expireToken(String token) {
        val currentWaitingToken = tokenReader.getByToken(token);
        currentWaitingToken.expire(timeManager.now());
        tokenWriter.save(currentWaitingToken);
    }
}
