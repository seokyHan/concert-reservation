package com.server.concert_reservation.infrastructure.db.user.repository.core;

import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.infrastructure.db.user.entity.UserEntity;
import com.server.concert_reservation.infrastructure.db.user.entity.WalletEntity;
import com.server.concert_reservation.infrastructure.db.user.repository.UserJpaRepository;
import com.server.concert_reservation.infrastructure.db.user.repository.WalletJpaRepository;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Repository
public class UserCoreReader implements UserReader {

    private final WalletJpaRepository walletJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public User getById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserEntity::toDomain)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletJpaRepository.findByUserId(userId)
                .map(WalletEntity::toDomain)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Override
    public Wallet getWalletByUserIdWithLock(Long userId) {
        return walletJpaRepository.findWithPessimisticLockById(userId)
                .map(WalletEntity::toDomain)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
