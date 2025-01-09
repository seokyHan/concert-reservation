package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.infrastructure.types.SeatStatus;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_seat")
public class ConcertSeatEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_schedule_id")
    private Long concertScheduleId;

    @Column(name = "number")
    private int number;

    @Column(name = "price")
    private int price;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @Builder
    public ConcertSeatEntity(Long id, Long concertScheduleId, int number, int price, SeatStatus status) {
        this.id = id;
        this.concertScheduleId = concertScheduleId;
        this.number = number;
        this.price = price;
        this.status = status;
    }

    public ConcertSeat toDomain() {
        return ConcertSeat.of(id, concertScheduleId, number, price, status, createdAt, updatedAt);
    }
}
