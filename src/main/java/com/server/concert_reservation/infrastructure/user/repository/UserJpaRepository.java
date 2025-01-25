package com.server.concert_reservation.infrastructure.user.repository;

import com.server.concert_reservation.infrastructure.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
