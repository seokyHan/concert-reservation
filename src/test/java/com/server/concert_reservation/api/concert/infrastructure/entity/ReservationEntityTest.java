package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.api.concert.domain.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus.RESERVED;
import static org.junit.jupiter.api.Assertions.*;

class ReservationEntityTest {

    @DisplayName("Reservation 엔티티를 Reservation 도메인 모델로 변환한다.")
    @Test
    void reservationEntityCovertToReservationDomainTest() {
        //given
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id(1L)
                .userId(1L)
                .seatIds(List.of(2L))
                .status(RESERVED)
                .build();

        //when
        Reservation reservation = reservationEntity.toDomain();

        //then
        assertEquals(reservationEntity.getId(), reservation.getId());
        assertEquals(reservationEntity.getUserId(), reservation.getUserId());
        assertEquals(reservationEntity.getSeatIds().get(0), reservation.getSeatIds().get(0));
        assertEquals(reservationEntity.getStatus(), reservation.getStatus());

    }

}