package com.server.concert_reservation.api.concert.domain.model;

import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertSeatEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.concert.domain.errorcode.ConcertErrorCode.CAN_NOT_RESERVE_SEAT;
import static com.server.concert_reservation.api.concert.domain.errorcode.ConcertErrorCode.RESERVING_ONLY;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConcertSeat {

    private Long id;
    private Long concertScheduleId;
    private int number;
    private int price;
    private SeatStatus status;
    private Long version;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public static ConcertSeat of(Long id,
                                 Long concertScheduleId,
                                 int number,
                                 int price,
                                 SeatStatus status,
                                 Long version,
                                 LocalDateTime createAt,
                                 LocalDateTime updatedAt) {

        return new ConcertSeat(id, concertScheduleId, number, price, status, version, createAt, updatedAt);
    }

    public ConcertSeatEntity toEntity(ConcertSeat concertSeat) {
        return ConcertSeatEntity.builder()
                .id(concertSeat.getId())
                .concertScheduleId(concertSeat.getConcertScheduleId())
                .number(concertSeat.getNumber())
                .price(concertSeat.getPrice())
                .status(concertSeat.getStatus())
                .version(concertSeat.getVersion())
                .build();
    }


    public void temporaryReserve() {
        if (this.status != AVAILABLE) {
            throw new CustomException(CAN_NOT_RESERVE_SEAT);
        }
        this.status = TEMPORARY_RESERVED;
    }

    public void confirm() {
        if (this.status != TEMPORARY_RESERVED) {
            throw new CustomException(RESERVING_ONLY);
        }

        this.status = SOLD;
    }

    public void cancel() {
        this.status = AVAILABLE;
    }

}
