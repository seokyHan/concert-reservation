package com.server.concert_reservation.api.user.domain.service;

import com.server.concert_reservation.api.user.application.PointUseCase;
import com.server.concert_reservation.api.user.domain.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.dto.command.UserCommand;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCommandService implements PointUseCase {

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
    @Transactional
    public WalletInfo chargePoint(UserCommand command) {
        val wallet = userReader.getWalletByUserId(command.userId());
        wallet.chargeAmount(command.point());

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }
}
