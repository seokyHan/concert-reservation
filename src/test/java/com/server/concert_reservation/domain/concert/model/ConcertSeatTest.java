package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.SOLD;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConcertSeatTest {

    private ConcertSeat concertSeat;

    @BeforeEach
    void setUp() {
        concertSeat = Instancio.of(ConcertSeat.class)
                .set(field(ConcertSeat::getId), 1L)
                .set(field(ConcertSeat::getConcertScheduleId), 2L)
                .set(field(ConcertSeat::getNumber), 10)
                .set(field(ConcertSeat::getPrice), 50000)
                .set(field(ConcertSeat::getStatus), AVAILABLE)
                .set(field(ConcertSeat::getVersion), 0L)
                .set(field(ConcertSeat::getCreateAt), LocalDateTime.now())
                .set(field(ConcertSeat::getUpdatedAt), null)
                .create();
    }

    @Test
    @DisplayName("임시 예약 후 좌석이 임시 예약 상태여야 한다")
    void shouldBeTemporaryReservedAfterReservation() {
        concertSeat.temporaryReserve();
        assertEquals(SeatStatus.TEMPORARY_RESERVED, concertSeat.getStatus());
    }

    @Test
    @DisplayName("이미 예약된 좌석은 다시 예약할 수 없어야 한다")
    void shouldNotAllowReservationOnAlreadyReservedSeat() {
        concertSeat.temporaryReserve();
        assertThrows(CustomException.class, () -> concertSeat.temporaryReserve());
    }

    @Test
    @DisplayName("구매 후 좌석이 판매 상태여야 한다")
    void shouldBeSoldAfterConfirmation() {
        //when
        concertSeat.temporaryReserve();
        concertSeat.confirm();

        //then
        assertEquals(SeatStatus.SOLD, concertSeat.getStatus());
    }

    @Test
    @DisplayName("임시 예약된 좌석만 확정할 수 있다. ")
    void shouldThrowExceptionWhenConfirmingWithoutReservation() {
        assertThrows(CustomException.class, () -> concertSeat.confirm());
    }

    @Test
    @DisplayName("임시 예약된 좌석만 취소 수 있다. ")
    void shouldThrowExceptionWhenCancelWithoutReservation() {
        ConcertSeat seat = ConcertSeat.builder()
                .status(SOLD)
                .build();
        assertThrows(CustomException.class, () -> seat.cancel());
    }

    @Test
    @DisplayName("취소 후 좌석이 사용 가능 상태여야 한다")
    void shouldBeAvailableAfterCancellation() {
        //when
        concertSeat.temporaryReserve();
        concertSeat.cancel();

        //then
        assertEquals(AVAILABLE, concertSeat.getStatus());
    }

}