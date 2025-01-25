package com.server.concert_reservation.api_backup.user.application;

import com.server.concert_reservation.api_backup.user.application.dto.UserCommand;
import com.server.concert_reservation.api_backup.user.application.dto.WalletInfo;

public interface UserCommandUseCase {

    WalletInfo chargePoint(UserCommand command);

    WalletInfo usePoint(UserCommand command);

}
