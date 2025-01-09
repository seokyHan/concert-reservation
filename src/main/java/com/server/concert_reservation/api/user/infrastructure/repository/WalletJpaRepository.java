package com.server.concert_reservation.api.user.infrastructure.repository;

import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByUserId(Long userId);
}
