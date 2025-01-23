package com.server.concert_reservation.api.user.domain.repository;

import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;

public interface UserReader {

    User getById(Long userId);

    Wallet getWalletByUserId(Long userId);

    Wallet getWalletByUserIdWithLock(Long userId);
}
