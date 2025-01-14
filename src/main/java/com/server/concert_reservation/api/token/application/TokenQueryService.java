package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.application.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenQueryService implements TokenQueryUseCase{

    private final TokenReader tokenReader;

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
}
