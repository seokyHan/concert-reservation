package com.server.concert_reservation.infrastructure.concert.entity;

import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ConcertScheduleEntityTest {

    @DisplayName("ConcertSchedule 엔티티를 ConcertSchedule 도메인 모델로 변환한다.")
    @Test
    void concertScheduleEntityCovertToConcertScheduleDomain() {
        //given
        ConcertScheduleEntity concertScheduleEntity = Instancio.create(ConcertScheduleEntity.class);

        //when
        ConcertSchedule concertSchedule = concertScheduleEntity.toDomain();

        //then
        assertEquals(concertScheduleEntity.getId(), concertSchedule.getId());
        assertEquals(concertScheduleEntity.getConcertId(), concertSchedule.getConcertId());
        assertEquals(concertScheduleEntity.getRemainTicket(), concertSchedule.getRemainTicket());
        assertEquals(concertScheduleEntity.getReservationStartAt(), concertSchedule.getReservationStartAt());
        assertEquals(concertScheduleEntity.getReservationEndAt(), concertSchedule.getReservationEndAt());

    }

}