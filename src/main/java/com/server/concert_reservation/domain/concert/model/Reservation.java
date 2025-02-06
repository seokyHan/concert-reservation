package com.server.concert_reservation.domain.concert.model;

import com.server.concert_reservation.domain.concert.errorcode.ConcertErrorCode;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    private Long id;
    private Long userId;
    private Long concertScheduleId;
    private List<Long> seatIds;
    private ReservationStatus status;
    private int totalPrice;
    private LocalDateTime reservationAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Reservation of(Long id,
                                 Long userId,
                                 Long concertScheduleId,
                                 List<Long> seatIds,
                                 ReservationStatus status,
                                 int totalPrice,
                                 LocalDateTime reservationAt,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt) {

        return new Reservation(id, userId, concertScheduleId, seatIds, status, totalPrice, reservationAt, createdAt, updatedAt);
    }

    public ReservationEntity toEntity(Reservation reservation) {
        return ReservationEntity.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .concertScheduleId(reservation.getConcertScheduleId())
                .seatIds(reservation.getSeatIds())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .reservationAt(reservation.getReservationAt())
                .build();
    }

    public void isTemporaryReserved() {
        if (this.status != ReservationStatus.RESERVING) {
            throw new CustomException(ConcertErrorCode.IS_NOT_TEMPORARY_RESERVATION);
        }
    }

    public void complete() {
        if (this.status != ReservationStatus.RESERVING) {
            throw new CustomException(ConcertErrorCode.PAYMENT_ONLY_FOR_TEMP_RESERVATION);
        }

        this.status = ReservationStatus.RESERVED;
    }

    public void cancelTemporaryReservation() {
        if (this.status != ReservationStatus.RESERVING) {
            throw new CustomException(ConcertErrorCode.CANCEL_ONLY_FOR_TEMP_RESERVATION);
        }

        this.status = ReservationStatus.CANCELED;
    }
}
