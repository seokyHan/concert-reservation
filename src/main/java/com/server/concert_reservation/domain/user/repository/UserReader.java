package com.server.concert_reservation.domain.user.repository;

import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;

public interface UserReader {

    User getById(Long userId);

    Wallet getWalletByUserId(Long userId);

    Wallet getWalletByUserIdWithLock(Long userId);
}
