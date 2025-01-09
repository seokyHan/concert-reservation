package com.server.concert_reservation.api.concert.infrastructure.entity;


import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.infrastructure.types.ReservationStatus;
import com.server.concert_reservation.common.converter.ListConverter;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_seat")
public class ReservationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "seat_ids")
    @Convert(converter = ListConverter.class)
    private List<Long> seatIds;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "reservation_at")
    private LocalDateTime reservationAt;

    @Builder
    public ReservationEntity(Long id, Long userId, List<Long> seatIds, ReservationStatus status, int totalPrice, LocalDateTime reservationAt) {
        this.id = id;
        this.userId = userId;
        this.seatIds = seatIds;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservationAt = reservationAt;
    }

    public Reservation toDomain() {
        return Reservation.of(id, userId, seatIds, status, totalPrice, reservationAt, createdAt, updatedAt);
    }
}
