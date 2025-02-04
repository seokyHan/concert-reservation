package com.server.concert_reservation.domain.user;

import com.server.concert_reservation.domain.user.dto.WalletInfo;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCommandService {

    private final UserWriter userWriter;
    private final UserReader userReader;

    public WalletInfo usePoint(Long userId, int point) {
        val wallet = userReader.getWalletByUserId(userId);
        wallet.useAmount(point);

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }

    public WalletInfo chargePoint(Long userId, int point) {
        val wallet = userReader.getWalletByUserIdWithLock(userId);
        wallet.chargeAmount(point);

        return WalletInfo.from(userWriter.saveUserPoint(wallet.toEntity(wallet)));
    }
}
