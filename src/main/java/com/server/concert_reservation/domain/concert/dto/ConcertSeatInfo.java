package com.server.concert_reservation.domain.concert.dto;

import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus;

import java.time.LocalDateTime;

public record ConcertSeatInfo(Long id,
                              Long concertScheduleId,
                              int number,
                              int price,
                              SeatStatus status,
                              Long version,
                              LocalDateTime createAt,
                              LocalDateTime updatedAt) {

    public static ConcertSeatInfo from(ConcertSeat concertSeat) {
        return new ConcertSeatInfo(concertSeat.getId(),
                concertSeat.getConcertScheduleId(),
                concertSeat.getNumber(),
                concertSeat.getPrice(),
                concertSeat.getStatus(),
                concertSeat.getVersion(),
                concertSeat.getCreateAt(),
                concertSeat.getUpdatedAt()
        );
    }

}
