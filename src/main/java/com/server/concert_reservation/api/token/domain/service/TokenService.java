package com.server.concert_reservation.api.token.domain.service;

import com.server.concert_reservation.api.token.application.TokenUseCase;
import com.server.concert_reservation.api.token.domain.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.dto.command.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.api.token.domain.repository.TokenWriter;
import com.server.concert_reservation.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class TokenService implements TokenUseCase {

    private final TokenReader tokenReader;
    private final TokenWriter tokenWriter;
    private final TimeManager timeManager;

    @Override
    public Token createToken(TokenCommand command) {
        return tokenWriter.save(new Token(command.userId(), command.token()));
    }

    @Override
    public TokenInfo getWaitingToken(String token, Long userId) {
        val currentWaitingToken = tokenReader.getByUserIdAndToken(userId, token);

        if (currentWaitingToken.isWaiting()) {
            val waitingOrder = tokenReader.getLatestActiveToken()
                    .map(latestActivatedToken -> currentWaitingToken.getId() - latestActivatedToken.getId())
                    .orElse(currentWaitingToken.getId());

            return TokenInfo.from(currentWaitingToken, waitingOrder);

        }
        return TokenInfo.from(currentWaitingToken, 0L);
    }


    /**
     * 현재 대기열이 활성화 상태인지 확인. 각 요청 전에 대기열 상태를 활성화 상태인지 확인할 때 사용
     */
    @Override
    public void checkActivatedToken(String token) {
        val tokenInfo = tokenReader.getByToken(token);
        tokenInfo.validateToken();
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
     * 가장 오래된 대기상태 토큰 목록 조회
     */
    @Override
    public List<Token> getWaitingToken(int activeCount) {
        return tokenReader.getWaitingToken(activeCount);
    }

    /**
     * 만료 처리 예정 대기열 토큰 목록 조회
     */
    @Override
    public List<Token> getWaitingTokenToBeExpired(int minutes) {
        return tokenReader.getWaitingTokenToBeExpired(minutes);
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
