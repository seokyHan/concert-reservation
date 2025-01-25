package com.server.concert_reservation.api_backup.user.application;

import com.server.concert_reservation.api_backup.user.application.dto.UserCommand;
import com.server.concert_reservation.api_backup.user.application.dto.WalletInfo;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.domain.user.repository.UserWriter;
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
        val wallet = userReader.getWalletByUserIdWithLock(command.userId());
        wallet.useAmount(command.point());

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }

    @Override
    @Transactional
    public WalletInfo chargePoint(UserCommand command) {
        val wallet = userReader.getWalletByUserIdWithLock(command.userId());
        wallet.chargeAmount(command.point());

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }
}
