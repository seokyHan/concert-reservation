package com.server.concert_reservation.infrastructure.concert.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.infrastructure.concert.entity.ConcertScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.infrastructure.concert.entity.QConcertScheduleEntity.concertScheduleEntity;

@RequiredArgsConstructor
@Repository
public class ConcertScheduleCoreQueryDsl implements ConcertScheduleQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime) {
        return queryFactory.selectFrom(concertScheduleEntity)
                .where(concertScheduleEntity.concertId.eq(concertId)
                        .and(concertScheduleEntity.reservationStartAt.lt(dateTime))
                        .and(concertScheduleEntity.remainTicket.gt(0)))
                .fetch();

    }
}
