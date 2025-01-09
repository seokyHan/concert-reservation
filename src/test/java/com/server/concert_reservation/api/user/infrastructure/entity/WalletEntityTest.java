package com.server.concert_reservation.api.user.infrastructure.entity;


import com.server.concert_reservation.api.user.domain.model.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class WalletEntityTest {

    @DisplayName("Wallet 엔티티를 Wallet 도메인 모델로 변환한다.")
    @Test
    void walletEntityCovertToWalletDomainTest() {
        //given
        WalletEntity walletEntity = WalletEntity.builder()
                .id(1L)
                .userId(1L)
                .amount(3000)
                .build();

        //when
        Wallet wallet = walletEntity.toDomain();

        //then
        assertEquals(walletEntity.getId(), wallet.getId());
        assertEquals(walletEntity.getUserId(), wallet.getUserId());
        assertEquals(walletEntity.getAmount(), wallet.getAmount());

    }

}