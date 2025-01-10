package com.server.concert_reservation.api.concert.infrastructure.entity;

import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.support.domain.auditing.BaseTimeEntity;
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
