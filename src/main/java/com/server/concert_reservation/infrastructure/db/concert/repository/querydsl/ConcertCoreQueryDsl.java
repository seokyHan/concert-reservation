package com.server.concert_reservation.infrastructure.db.concert.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertScheduleEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.infrastructure.db.concert.entity.QConcertScheduleEntity.concertScheduleEntity;
import static com.server.concert_reservation.infrastructure.db.concert.entity.QReservationOutboxEntity.reservationOutboxEntity;

@RequiredArgsConstructor
@Repository
public class ConcertCoreQueryDsl implements ConcertQueryDsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConcertScheduleEntity> findGetAvailableConcertSchedule(Long concertId, LocalDateTime dateTime) {
        return queryFactory.selectFrom(concertScheduleEntity)
                .where(concertScheduleEntity.concertId.eq(concertId)
                        .and(concertScheduleEntity.reservationStartAt.lt(dateTime))
                        .and(concertScheduleEntity.remainTicket.gt(0)))
                .fetch();

    }

    @Override
    public List<ReservationOutboxEntity> findAllReservationPendingOutboxMessage(LocalDateTime dateTime) {
        return queryFactory.selectFrom(reservationOutboxEntity)
                .where(reservationOutboxEntity.status.eq(OutboxStatus.INIT)
                        .and(reservationOutboxEntity.createdAt.lt(dateTime)))
                .fetch();
    }
}
