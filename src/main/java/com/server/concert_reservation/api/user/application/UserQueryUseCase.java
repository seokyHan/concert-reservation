package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.application.dto.WalletInfo;
import com.server.concert_reservation.api.user.application.dto.UserCommand;

public interface UserQueryUseCase {

    WalletInfo chargePoint(UserCommand command);
    WalletInfo usePoint(UserCommand command);

}
