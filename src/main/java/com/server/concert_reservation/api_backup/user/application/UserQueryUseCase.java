package com.server.concert_reservation.api_backup.user.application;

import com.server.concert_reservation.api_backup.user.application.dto.UserInfo;
import com.server.concert_reservation.api_backup.user.application.dto.WalletInfo;

public interface UserQueryUseCase {

    UserInfo getUser(Long userId);

    WalletInfo getWallet(Long userId);
}
