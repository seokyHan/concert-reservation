package com.server.concert_reservation.api.concert.infrastructure.entity;


import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
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

    @Column(name = "reservation_end_at")
    private LocalDateTime reservationEndAt;



    @Builder
    public ConcertScheduleEntity(Long id, Long concertId, int remainTicket, LocalDateTime reservationStartAt, LocalDateTime reservationEndAt) {
        this.id = id;
        this.concertId = concertId;
        this.remainTicket = remainTicket;
        this.reservationStartAt = reservationStartAt;
        this.reservationEndAt = reservationEndAt;
    }

    public ConcertSchedule toDomain() {
        return ConcertSchedule.of(id, concertId, remainTicket, reservationStartAt, reservationEndAt, createdAt, updatedAt);
    }
}
