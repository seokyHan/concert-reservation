package com.server.concert_reservation.api.concert.domain.model;

import com.server.concert_reservation.api.concert.infrastructure.types.SeatStatus;
import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConcertSeatTest {

    private ConcertSeat concertSeat;

    @BeforeEach
    void setUp() {
        concertSeat = ConcertSeat.of(
                1L,
                100L,
                10,
                50000,
                SeatStatus.AVAILABLE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("좌석이 사용 가능해야 한다.")
    void isAvailableTest() {
        assertTrue(concertSeat.isAvailable());
    }

    @Test
    @DisplayName("임시 예약 후 좌석이 임시 예약 상태여야 한다")
    void temporaryReserveTest() {
        concertSeat.temporaryReserve();
        assertEquals(SeatStatus.TEMPORARY_RESERVED, concertSeat.getStatus());
    }

    @Test
    @DisplayName("이미 예약된 좌석은 다시 예약할 수 없어야 한다")
    void temporaryReserve_WhenNotAvailableTest() {
        concertSeat.temporaryReserve();
        assertThrows(CustomException.class, () -> concertSeat.temporaryReserve());
    }

    @Test
    @DisplayName("구매 후 좌석이 판매 상태여야 한다")
    void confirmTest() {
        //when
        concertSeat.temporaryReserve();
        concertSeat.confirm();

        //then
        assertEquals(SeatStatus.SOLD, concertSeat.getStatus());
    }

    @Test
    @DisplayName("임시 예약된 좌석만 확정할 수 있다. ")
    void confirm_WhenNotReservingTest() {
        assertThrows(CustomException.class, () -> concertSeat.confirm());
    }

    @Test
    @DisplayName("취소 후 좌석이 사용 가능 상태여야 한다")
    void cancelTest() {
        //when
        concertSeat.temporaryReserve();
        concertSeat.cancel();

        //then
        assertEquals(SeatStatus.AVAILABLE, concertSeat.getStatus());
    }

}