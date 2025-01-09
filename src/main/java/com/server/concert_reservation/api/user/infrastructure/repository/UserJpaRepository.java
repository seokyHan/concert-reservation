package com.server.concert_reservation.api.user.infrastructure.repository;

import com.server.concert_reservation.api.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
