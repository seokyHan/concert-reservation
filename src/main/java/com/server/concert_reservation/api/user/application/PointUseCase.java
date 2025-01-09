package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.domain.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.dto.command.UserCommand;

public interface PointUseCase {

    WalletInfo chargePoint(UserCommand command);
    WalletInfo usePoint(UserCommand command);

}
