package com.server.concert_reservation.infrastructure.concert.entity;

import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ConcertSeatEntityTest {

    @DisplayName("ConcertSeat 엔티티를 ConcertSeat 도메인 모델로 변환한다.")
    @Test
    void concertSeatEntityCovertToConcertSeatDomain() {
        //given
        ConcertSeatEntity concertSeatEntity = Instancio.create(ConcertSeatEntity.class);

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