package com.server.concert_reservation.api.token.infrastructure.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.api.token.infrastructure.entity.QTokenEntity;
import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.server.concert_reservation.api.token.infrastructure.types.TokenStatus.ACTIVE;
import static com.server.concert_reservation.api.token.infrastructure.types.TokenStatus.WAITING;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

@Repository
@RequiredArgsConstructor
public class TokenCoreQueryDsl implements TokenQueryDsl{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<TokenEntity> getLatestActivatedToken() {
        val token = QTokenEntity.tokenEntity;
        val result = jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(ACTIVE))
                .orderBy(token.id.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<TokenEntity> findWaitingToken(int activationCount) {
        val token = QTokenEntity.tokenEntity;
        return jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(WAITING))
                .orderBy(token.id.asc())
                .limit(activationCount)
                .fetch();
    }

    @Override
    public List<TokenEntity> findWaitingTokenToBeExpired(LocalDateTime expiredAt) {
        val token = QTokenEntity.tokenEntity;
        return jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(ACTIVE)
                        .and(token.activatedAt.loe(expiredAt)))
                .fetch();
    }
}
