package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

class ConcertScheduleEntityTest {

    @DisplayName("ConcertSchedule 엔티티를 ConcertSchedule 도메인 모델로 변환한다.")
    @Test
    void concertScheduleEntityCovertToConcertScheduleDomainTest() {
        //given
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now().plusDays(1L);

        ConcertScheduleEntity concertScheduleEntity = ConcertScheduleEntity.builder()
                .id(1L)
                .concertId(1L)
                .remainTicket(3)
                .reservationStartAt(startAt)
                .reservationEndAt(endAt)
                .build();

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