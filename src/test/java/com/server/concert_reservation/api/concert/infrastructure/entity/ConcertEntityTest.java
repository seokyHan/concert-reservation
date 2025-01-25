package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.domain.concert.model.Concert;
import com.server.concert_reservation.infrastructure.concert.entity.ConcertEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcertEntityTest {

    @DisplayName("Concert 엔티티를 Concert 도메인 모델로 변환한다.")
    @Test
    void concertEntityCovertToConcertDomainTest() {
        //given
        ConcertEntity concertEntity = ConcertEntity.builder()
                .id(1L)
                .title("title")
                .description("description")
                .build();

        //when
        Concert concert = concertEntity.toDomain();

        //then
        assertEquals(concertEntity.getId(), concert.getId());
        assertEquals(concertEntity.getTitle(), concert.getTitle());
        assertEquals(concertEntity.getDescription(), concert.getDescription());

    }

}