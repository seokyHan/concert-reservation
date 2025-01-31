package com.server.concert_reservation.infrastructure.concert.entity;


import com.server.concert_reservation.infrastructure.auditing.BaseTimeEntity;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.infrastructure.concert.entity.converter.ListConverter;
import com.server.concert_reservation.infrastructure.concert.entity.types.ReservationStatus;
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
@Table(name = "reservation")
public class ReservationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "concert_schedule_id")
    private Long concertScheduleId;

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
    public ReservationEntity(Long id, Long userId, Long concertScheduleId, List<Long> seatIds, ReservationStatus status, int totalPrice, LocalDateTime reservationAt) {
        this.id = id;
        this.userId = userId;
        this.concertScheduleId = concertScheduleId;
        this.seatIds = seatIds;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservationAt = reservationAt;
    }

    public Reservation toDomain() {
        return Reservation.of(id, userId, concertScheduleId, seatIds, status, totalPrice, reservationAt, createdAt, updatedAt);
    }
}
