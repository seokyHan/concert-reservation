package com.server.concert_reservation.infrastructure.db.concert.entity;

import com.server.concert_reservation.infrastructure.db.auditing.BaseTimeEntity;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Version
    private Long version;


    @Builder
    public ConcertSeatEntity(Long id, Long concertScheduleId, int number, int price, SeatStatus status, Long version) {
        this.id = id;
        this.concertScheduleId = concertScheduleId;
        this.number = number;
        this.price = price;
        this.status = status;
        this.version = version;
    }

    public ConcertSeat toDomain() {
        return ConcertSeat.of(id, concertScheduleId, number, price, status, version, createdAt, updatedAt);
    }
}
