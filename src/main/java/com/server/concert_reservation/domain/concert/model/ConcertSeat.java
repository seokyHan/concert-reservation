package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode;
import com.server.concert_reservation.infrastructure.concert.entity.ConcertSeatEntity;
import com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.*;

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
            throw new CustomException(ConcertErrorCode.CAN_NOT_RESERVE_SEAT);
        }
        this.status = TEMPORARY_RESERVED;
    }

    public void confirm() {
        if (this.status != TEMPORARY_RESERVED) {
            throw new CustomException(ConcertErrorCode.RESERVING_ONLY);
        }

        this.status = SOLD;
    }

    public void cancel() {
        if (this.status == SOLD) {
            throw new CustomException(ConcertErrorCode.ALREADY_SOLD_SEAT);
        }

        this.status = AVAILABLE;
    }

}
