package com.server.concert_reservation.infrastructure.queue_token.repository.core;

import com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import com.server.concert_reservation.infrastructure.queue_token.repository.QueueTokenJpaRepository;
import com.server.concert_reservation.infrastructure.queue_token.repository.querydsl.QueueTokenQueryDsl;
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
public class QueueTokenCoreReader implements QueueTokenReader {

    private final QueueTokenJpaRepository queueTokenJpaRepository;
    private final QueueTokenQueryDsl queueTokenQueryDsl;
    private final TimeManager timeManager;

    @Override
    public QueueToken getByToken(String token) {
        return queueTokenJpaRepository.findByToken(token)
                .map(QueueTokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(QueueTokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public QueueToken getByUserIdAndToken(Long userId, String token) {
        return queueTokenJpaRepository.findByUserIdAndToken(userId, token)
                .map(QueueTokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(QueueTokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Optional<QueueToken> getLatestActiveToken() {
        return queueTokenQueryDsl.findLatestActivatedToken()
                .map(QueueTokenEntity::toDomain);
    }

    @Override
    public List<QueueToken> getWaitingTokensToBeExpired(int minutes) {
        val expiredAt = timeManager.now().minusMinutes(minutes);
        val queueToken = queueTokenQueryDsl.findWaitingTokenToBeExpired(expiredAt);

        return queueToken.isEmpty() ?
                List.of() :
                queueToken
                        .stream()
                        .map(QueueTokenEntity::toDomain)
                        .collect(Collectors.toList());
    }

    @Override
    public List<QueueToken> getWaitingToken(int activeCount) {
        val queueToken = queueTokenQueryDsl.findWaitingToken(activeCount);

        return queueToken.isEmpty() ?
                List.of() :
                queueToken
                        .stream()
                        .map(QueueTokenEntity::toDomain)
                        .collect(Collectors.toList());
    }
}


