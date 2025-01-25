package com.server.concert_reservation.api_backup.token.application;

import com.server.concert_reservation.domain.queue_token.model.Token;
import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;
import com.server.concert_reservation.domain.queue_token.repository.TokenReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenQueryService implements TokenQueryUseCase {

    private final TokenReader tokenReader;

    @Override
    public TokenInfo getWaitingToken(String token, Long userId) {
        val waitingToken = tokenReader.getByUserIdAndToken(userId, token);

        if (waitingToken.isWaiting()) {
            val waitingOrder = tokenReader.getLatestActiveToken()
                    .map(latestActivatedToken -> waitingToken.getId() - latestActivatedToken.getId())
                    .orElse(waitingToken.getId());

            return TokenInfo.from(waitingToken, waitingOrder);

        }
        return TokenInfo.from(waitingToken, 0L);
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
    public List<Token> getWaitingTokensToBeExpired(int minutes) {
        return tokenReader.getWaitingTokensToBeExpired(minutes);
    }
}
