package com.server.concert_reservation.infrastructure.concert.entity;


import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.infrastructure.auditing.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_schedule")
public class ConcertScheduleEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concert_id")
    private Long concertId;

    @Column(name = "remain_ticket")
    private int remainTicket;

    @Column(name = "reservation_start_at")
    private LocalDateTime reservationStartAt;

    @Builder
    public ConcertScheduleEntity(Long id, Long concertId, int remainTicket, LocalDateTime reservationStartAt) {
        this.id = id;
        this.concertId = concertId;
        this.remainTicket = remainTicket;
        this.reservationStartAt = reservationStartAt;
    }

    public ConcertScheduleEntity(ConcertSchedule concertSchedule) {
        this.id = concertSchedule.getId();
        this.concertId = concertSchedule.getConcertId();
        this.remainTicket = concertSchedule.getRemainTicket();
        this.reservationStartAt = concertSchedule.getReservationStartAt();
    }

    public ConcertSchedule toDomain() {
        return ConcertSchedule.of(id, concertId, remainTicket, reservationStartAt, createdAt, updatedAt);
    }
}
