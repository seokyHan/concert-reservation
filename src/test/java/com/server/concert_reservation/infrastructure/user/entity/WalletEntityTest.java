package com.server.concert_reservation.infrastructure.user.entity;


import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.infrastructure.db.user.entity.WalletEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class WalletEntityTest {

    @DisplayName("Wallet 엔티티를 Wallet 도메인 모델로 변환한다.")
    @Test
    void walletEntityCovertToWalletDomain() {
        //given
        WalletEntity walletEntity = Instancio.create(WalletEntity.class);

        //when
        Wallet wallet = walletEntity.toDomain();

        //then
        assertEquals(walletEntity.getId(), wallet.getId());
        assertEquals(walletEntity.getUserId(), wallet.getUserId());
        assertEquals(walletEntity.getAmount(), wallet.getAmount());

    }

}