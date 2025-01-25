package com.server.concert_reservation.infrastructure.queue_token.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.api_backup.token.infrastructure.entity.QTokenEntity;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;

@Repository
@RequiredArgsConstructor
public class TokenCoreQueryDsl implements TokenQueryDsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<QueueTokenEntity> getLatestActivatedToken() {
        val token = QTokenEntity.tokenEntity;
        val result = jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(ACTIVE))
                .orderBy(token.id.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<QueueTokenEntity> findWaitingToken(int activationCount) {
        val token = QTokenEntity.tokenEntity;
        return jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(WAITING))
                .orderBy(token.id.asc())
                .limit(activationCount)
                .fetch();
    }

    @Override
    public List<QueueTokenEntity> findWaitingTokenToBeExpired(LocalDateTime expiredAt) {
        val token = QTokenEntity.tokenEntity;
        return jpaQueryFactory.selectFrom(token)
                .where(token.status.eq(ACTIVE)
                        .and(token.activatedAt.loe(expiredAt)))
                .fetch();
    }
}
