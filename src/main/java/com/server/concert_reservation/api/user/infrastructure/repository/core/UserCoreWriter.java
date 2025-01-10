package com.server.concert_reservation.api.user.infrastructure.repository.core;


import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.entity.UserEntity;
import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import com.server.concert_reservation.api.user.infrastructure.repository.WalletJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserCoreWriter implements UserWriter {

    private final WalletJpaRepository walletJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(new UserEntity(user.getId(), user.getName()))
                .toDomain();
    }

    @Override
    public Wallet saveUserPoint(WalletEntity walletEntity) {
        return walletJpaRepository.save(walletEntity).toDomain();
    }

}
