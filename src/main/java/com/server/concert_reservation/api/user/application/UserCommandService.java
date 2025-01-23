package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.api.user.application.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.support.api.common.aop.annotation.DistributedLock;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCommandService implements UserCommandUseCase {

    private final UserWriter userWriter;
    private final UserReader userReader;

    @Override
    @Transactional
    public WalletInfo usePoint(UserCommand command) {
        val wallet = userReader.getWalletByUserId(command.userId());
        wallet.useAmount(command.point());

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }

    @Override
    @DistributedLock(prefix = "pointCharge", key = "#command.userId", waitTime = 500)
    public WalletInfo chargePoint(UserCommand command) {
        val wallet = userReader.getWalletByUserId(command.userId());
        wallet.chargeAmount(command.point());

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }
}
