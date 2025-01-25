package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.infrastructure.concert.entity.ConcertSeatEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.SOLD;
import static org.junit.Assert.assertEquals;

class ConcertSeatEntityTest {

    @DisplayName("ConcertSeat 엔티티를 ConcertSeat 도메인 모델로 변환한다.")
    @Test
    void concertSeatEntityCovertToConcertSeatDomainTest() {
        //given
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(1L);

        ConcertSeatEntity concertSeatEntity = ConcertSeatEntity.builder()
                .id(1L)
                .concertScheduleId(1L)
                .number(3)
                .price(50000)
                .status(SOLD)
                .build();

        //when
        ConcertSeat concertSeat = concertSeatEntity.toDomain();

        //then
        assertEquals(concertSeatEntity.getId(), concertSeat.getId());
        assertEquals(concertSeatEntity.getConcertScheduleId(), concertSeat.getConcertScheduleId());
        assertEquals(concertSeatEntity.getNumber(), concertSeat.getNumber());
        assertEquals(concertSeatEntity.getPrice(), concertSeat.getPrice());
        assertEquals(concertSeatEntity.getStatus(), concertSeat.getStatus());

    }

}