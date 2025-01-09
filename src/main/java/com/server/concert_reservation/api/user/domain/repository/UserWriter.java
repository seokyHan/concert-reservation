package com.server.concert_reservation.api.user.domain.repository;

import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;

public interface UserWriter {

    Wallet saveUserPoint(WalletEntity walletEntity);
}