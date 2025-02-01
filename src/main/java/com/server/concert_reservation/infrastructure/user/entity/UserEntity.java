package com.server.concert_reservation.infrastructure.user.entity;

import com.server.concert_reservation.infrastructure.auditing.BaseTimeEntity;
import com.server.concert_reservation.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"user\"")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder
    public UserEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User toDomain() {
        return User.of(id, name, createdAt, updatedAt);
    }


}
