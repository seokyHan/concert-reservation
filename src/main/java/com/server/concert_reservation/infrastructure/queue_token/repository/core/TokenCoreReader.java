package com.server.concert_reservation.infrastructure.queue_token.repository.core;

import com.server.concert_reservation.domain.queue_token.errorcode.TokenErrorCode;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import com.server.concert_reservation.infrastructure.queue_token.repository.TokenJpaRepository;
import com.server.concert_reservation.infrastructure.queue_token.repository.querydsl.TokenQueryDsl;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class TokenCoreReader implements QueueTokenReader {

    private final TokenJpaRepository tokenJpaRepository;
    private final TokenQueryDsl tokenQueryDsl;
    private final TimeManager timeManager;

    @Override
    public QueueToken getByToken(String token) {
        return tokenJpaRepository.findByToken(token)
                .map(QueueTokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(TokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public QueueToken getByUserIdAndToken(Long userId, String token) {
        return tokenJpaRepository.findByUserIdAndToken(userId, token)
                .map(QueueTokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(TokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Optional<QueueToken> getLatestActiveToken() {
        return tokenQueryDsl.getLatestActivatedToken()
                .map(QueueTokenEntity::toDomain);
    }

    @Override
    public List<QueueToken> getWaitingTokensToBeExpired(int minutes) {
        val expiredAt = timeManager.now().minusMinutes(minutes);

        return tokenQueryDsl.findWaitingTokenToBeExpired(expiredAt)
                .stream()
                .map(QueueTokenEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueToken> getWaitingToken(int activeCount) {
        return tokenQueryDsl.findWaitingToken(activeCount)
                .stream()
                .map(QueueTokenEntity::toDomain)
                .collect(Collectors.toList());
    }
}


