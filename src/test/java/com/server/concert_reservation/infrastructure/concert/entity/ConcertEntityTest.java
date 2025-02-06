package com.server.concert_reservation.infrastructure.concert.entity;

import com.server.concert_reservation.domain.concert.model.Concert;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConcertEntityTest {

    @DisplayName("Concert 엔티티를 Concert 도메인 모델로 변환한다.")
    @Test
    void concertEntityCovertToConcertDomain() {
        //given
        ConcertEntity concertEntity = Instancio.create(ConcertEntity.class);

        //when
        Concert concert = concertEntity.toDomain();

        //then
        assertEquals(concertEntity.getId(), concert.getId());
        assertEquals(concertEntity.getTitle(), concert.getTitle());
        assertEquals(concertEntity.getDescription(), concert.getDescription());

    }

}