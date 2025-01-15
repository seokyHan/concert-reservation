package com.server.concert_reservation.api.user.infrastructure.repository.core;

import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;
import com.server.concert_reservation.api.user.infrastructure.entity.UserEntity;
import com.server.concert_reservation.api.user.infrastructure.repository.WalletJpaRepository;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.server.concert_reservation.api.user.domain.errorcode.UserErrorCode.USER_NOT_FOUND;

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
}
