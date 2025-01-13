package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.domain.model.dto.UserInfo;
import com.server.concert_reservation.api.user.domain.model.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserQueryService implements UserCommandUseCase {

    private final UserReader userReader;

    @Override
    public UserInfo getUser(Long userId) {
        return UserInfo.from(userReader.getById(userId));
    }

    @Override
    public WalletInfo getWallet(Long userId) {
        val wallet = userReader.getWalletByUserId(userId);

        return WalletInfo.from(wallet);
    }
}
