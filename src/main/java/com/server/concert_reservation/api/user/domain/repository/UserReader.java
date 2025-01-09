package com.server.concert_reservation.api.user.domain.repository;

import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.model.User;

public interface UserReader {

    User getById(Long userId);
    Wallet getWalletByUserId(Long userId);
}
