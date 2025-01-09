package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.domain.dto.UserInfo;
import com.server.concert_reservation.api.user.domain.dto.WalletInfo;

public interface GetUserUseCase {

    UserInfo getUser(Long userId);
    WalletInfo getWallet(Long userId);
}
