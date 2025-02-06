package com.server.concert_reservation.domain.user.repository;

import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.infrastructure.db.user.entity.WalletEntity;

public interface UserWriter {

    User save(User user);

    Wallet saveUserPoint(WalletEntity walletEntity);

}
