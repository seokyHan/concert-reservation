package com.server.concert_reservation.infrastructure.concert.entity;

import com.server.concert_reservation.domain.concert.model.Reservation;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationEntityTest {

    @DisplayName("Reservation 엔티티를 Reservation 도메인 모델로 변환한다.")
    @Test
    void reservationEntityCovertToReservationDomain() {
        //given
        ReservationEntity reservationEntity = Instancio.create(ReservationEntity.class);

        //when
        Reservation reservation = reservationEntity.toDomain();

        //then
        assertEquals(reservationEntity.getId(), reservation.getId());
        assertEquals(reservationEntity.getUserId(), reservation.getUserId());
        assertEquals(reservationEntity.getSeatIds().get(0), reservation.getSeatIds().get(0));
        assertEquals(reservationEntity.getStatus(), reservation.getStatus());

    }

}