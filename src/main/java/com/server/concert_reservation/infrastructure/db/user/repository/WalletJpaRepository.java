package com.server.concert_reservation.infrastructure.db.user.repository;

import com.server.concert_reservation.infrastructure.db.user.entity.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {

    Optional<WalletEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<WalletEntity> findWithPessimisticLockById(Long userId);
}
