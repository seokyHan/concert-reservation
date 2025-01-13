package com.server.concert_reservation.api.concert.domain.model;

import com.server.concert_reservation.api.concert.domain.model.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.infrastructure.entity.ReservationEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus;
import com.server.concert_reservation.common.exception.CustomException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus.*;
import static com.server.concert_reservation.common.exception.code.ConcertErrorCode.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    private Long id;
    private Long userId;
    private List<Long> seatIds;
    private ReservationStatus status;
    private int totalPrice;
    private LocalDateTime reservationAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Reservation of(Long id,
                                 Long userId,
                                 List<Long> seatIds,
                                 ReservationStatus status,
                                 int totalPrice,
                                 LocalDateTime reservationAt,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt ) {

        return new Reservation(id, userId, seatIds, status, totalPrice, reservationAt, createdAt, updatedAt);
    }

    public static Reservation createReservation(ReservationCommand command, int totalPrice, LocalDateTime now) {
        return Reservation.builder()
                .userId(command.userId())
                .seatIds(command.seatIds())
                .status(RESERVING)
                .totalPrice(totalPrice)
                .reservationAt(now)
                .build();
    }

    public ReservationEntity toEntity(Reservation reservation, LocalDateTime dateTime) {
        return ReservationEntity.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .seatIds(reservation.getSeatIds())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .reservationAt(dateTime)
                .build();
    }

    public void isTemporaryReserved() {
        if (this.status != RESERVING) {
            throw new CustomException(IS_NOT_TEMPORARY_RESERVATION);
        }
    }

    public void complete() {
        if (this.status != RESERVING) {
            throw new CustomException(PAYMENT_ONLY_FOR_TEMP_RESERVATION);
        }

        this.status = RESERVED;
    }

    public void cancelTemporaryReservation() {
        if (this.status != RESERVING) {
            throw new CustomException(CANCEL_ONLY_FOR_TEMP_RESERVATION);
        }

        this.status = CANCELED;
    }
}
