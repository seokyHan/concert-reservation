package com.server.concert_reservation.domain.user;

import com.server.concert_reservation.domain.user.dto.UserInfo;
import com.server.concert_reservation.domain.user.dto.WalletInfo;
import com.server.concert_reservation.domain.user.repository.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserReader userReader;

    public UserInfo findUser(Long userId) {
        val user = userReader.getById(userId);

        return UserInfo.from(user);
    }

    public WalletInfo findWallet(Long userId) {
        val wallet = userReader.getWalletByUserId(userId);

        return WalletInfo.from(wallet);
    }
}
