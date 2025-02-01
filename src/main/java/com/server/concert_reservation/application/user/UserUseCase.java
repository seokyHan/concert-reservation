package com.server.concert_reservation.application.user;

import com.server.concert_reservation.application.user.dto.UserCommand;
import com.server.concert_reservation.application.user.dto.WalletResult;
import com.server.concert_reservation.domain.user.UserCommandService;
import com.server.concert_reservation.domain.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCase {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Transactional
    public WalletResult chargePoint(UserCommand command) {
        val user = userQueryService.findUser(command.userId());
        val wallet = userCommandService.chargePoint(user.id(), command.point());

        return WalletResult.from(wallet);
    }

    public WalletResult getWallet(Long userId) {
        val user = userQueryService.findUser(userId);
        val wallet = userQueryService.findWallet(user.id());

        return WalletResult.from(wallet);
    }
}
