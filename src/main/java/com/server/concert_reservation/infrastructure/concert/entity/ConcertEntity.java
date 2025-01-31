package com.server.concert_reservation.infrastructure.concert.entity;

import com.server.concert_reservation.infrastructure.auditing.BaseTimeEntity;
import com.server.concert_reservation.domain.concert.model.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert")
public class ConcertEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Builder
    public ConcertEntity(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public ConcertEntity(Concert concert) {
        this.id = concert.getId();
        this.title = concert.getTitle();
        this.description = concert.getDescription();
    }

    public Concert toDomain() {
        return Concert.of(id, title, description, createdAt, updatedAt);
    }
}
