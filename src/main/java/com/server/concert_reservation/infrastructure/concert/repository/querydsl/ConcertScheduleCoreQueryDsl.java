package com.server.concert_reservation.infrastructure.concert.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.infrastructure.concert.entity.ConcertScheduleEntity;
import com.server.concert_reservation.api_backup.concert.infrastructure.entity.QConcertScheduleEntity;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertScheduleCoreQueryDsl implements ConcertScheduleQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime) {
        val concertSchedule = QConcertScheduleEntity.concertScheduleEntity;
        return queryFactory.selectFrom(concertSchedule)
                .where(concertSchedule.concertId.eq(concertId)
                        .and(concertSchedule.reservationStartAt.lt(dateTime))
                        .and(concertSchedule.reservationEndAt.goe(dateTime))
                        .and(concertSchedule.remainTicket.gt(0)))
                .fetch();

    }
}
