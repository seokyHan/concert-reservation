package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.api.user.application.dto.WalletInfo;

public interface UserCommandUseCase {

    WalletInfo chargePoint(UserCommand command);

    WalletInfo usePoint(UserCommand command);

}
