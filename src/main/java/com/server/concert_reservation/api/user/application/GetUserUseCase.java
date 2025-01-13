package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.domain.model.dto.UserInfo;
import com.server.concert_reservation.api.user.domain.model.dto.WalletInfo;

public interface GetUserUseCase {

    UserInfo getUser(Long userId);
    WalletInfo getWallet(Long userId);
}
