package com.server.concert_reservation.infrastructure.queue_token.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.server.concert_reservation.infrastructure.queue_token.entity.QQueueTokenEntity.queueTokenEntity;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;

@Repository
@RequiredArgsConstructor
public class QueueTokenCoreQueryDsl implements QueueTokenQueryDsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<QueueTokenEntity> findLatestActivatedToken() {
        val result = jpaQueryFactory.selectFrom(queueTokenEntity)
                .where(queueTokenEntity.status.eq(ACTIVE))
                .orderBy(queueTokenEntity.id.desc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<QueueTokenEntity> findWaitingToken(int activationCount) {
        return jpaQueryFactory.selectFrom(queueTokenEntity)
                .where(queueTokenEntity.status.eq(WAITING))
                .orderBy(queueTokenEntity.id.asc())
                .limit(activationCount)
                .fetch();
    }

    @Override
    public List<QueueTokenEntity> findWaitingTokenToBeExpired(LocalDateTime expiredAt) {
        return jpaQueryFactory.selectFrom(queueTokenEntity)
                .where(queueTokenEntity.status.eq(ACTIVE)
                        .and(queueTokenEntity.activatedAt.loe(expiredAt)))
                .fetch();
    }
}
