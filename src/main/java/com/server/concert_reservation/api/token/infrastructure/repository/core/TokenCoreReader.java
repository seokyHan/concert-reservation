package com.server.concert_reservation.api.token.infrastructure.repository.core;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import com.server.concert_reservation.api.token.infrastructure.repository.TokenJpaRepository;
import com.server.concert_reservation.api.token.infrastructure.repository.querydsl.TokenQueryDsl;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.api.token.domain.errorcode.TokenErrorCode;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class TokenCoreReader implements TokenReader {

    private final TokenJpaRepository tokenJpaRepository;
    private final TokenQueryDsl tokenQueryDsl;
    private final TimeManager timeManager;

    @Override
    public Token getByToken(String token) {
        return tokenJpaRepository.findByToken(token)
                .map(TokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(TokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Token getByUserIdAndToken(Long userId, String token) {
        return tokenJpaRepository.findByUserIdAndToken(userId, token)
                .map(TokenEntity::toDomain)
                .orElseThrow(() -> new CustomException(TokenErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Optional<Token> getLatestActiveToken() {
        return tokenQueryDsl.getLatestActivatedToken()
                .map(TokenEntity::toDomain);
    }

    @Override
    public List<Token> getWaitingTokensToBeExpired(int minutes) {
        val expiredAt = timeManager.now().minusMinutes(minutes);

        return tokenQueryDsl.findWaitingTokenToBeExpired(expiredAt)
                .stream()
                .map(TokenEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Token> getWaitingToken(int activeCount) {
        return tokenQueryDsl.findWaitingToken(activeCount)
                .stream()
                .map(TokenEntity::toDomain)
                .collect(Collectors.toList());
    }
}


