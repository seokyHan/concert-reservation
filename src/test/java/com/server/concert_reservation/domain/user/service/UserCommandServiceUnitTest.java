package com.server.concert_reservation.domain.user.service;

import com.server.concert_reservation.domain.user.UserCommandService;
import com.server.concert_reservation.domain.user.dto.WalletInfo;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.infrastructure.user.entity.WalletEntity;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.NOT_ENOUGH_POINT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceUnitTest {

    @Mock
    UserReader userReader;
    @Mock
    UserWriter userWriter;
    @InjectMocks
    UserCommandService userCommandService;

    @DisplayName("유저의 포인트를 충전한다.")
    @Test
    void chargeUserPoint() {
        //given
        int point = 3000;
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getAmount), 2000)
                .create();
        when(userReader.getWalletByUserIdWithLock(wallet.getUserId())).thenReturn(wallet);
        when(userWriter.saveUserPoint(any(WalletEntity.class))).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userCommandService.chargePoint(wallet.getUserId(), point);

        //then
        assertEquals(updatedWallet.id(), wallet.getId());
        assertEquals(updatedWallet.userId(), wallet.getUserId());
        assertEquals(updatedWallet.amount(), 5000);
    }

    @DisplayName("유저의 포인트를 사용한다.")
    @Test
    void useUserPoint() {
        //given
        int point = 3000;
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getAmount), 3500)
                .create();
        when(userReader.getWalletByUserId(wallet.getUserId())).thenReturn(wallet);
        when(userWriter.saveUserPoint(any(WalletEntity.class))).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userCommandService.usePoint(wallet.getUserId(), point);

        //then
        assertEquals(updatedWallet.id(), wallet.getId());
        assertEquals(updatedWallet.userId(), wallet.getUserId());
        assertEquals(updatedWallet.amount(), 500);
    }

    @DisplayName("충전 포인트가 0원이거나 0원보다 작은 경우 예외가 발생한다. .")
    @Test
    void shouldThrowExceptionWhenZeroOrMinusPoint() {
        //given
        int point = 0;
        Wallet wallet = Instancio.create(Wallet.class);
        when(userReader.getWalletByUserIdWithLock(wallet.getUserId())).thenReturn(wallet);

        //then //when
        assertThatThrownBy(() -> userCommandService.chargePoint(wallet.getUserId(), point))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }

    @DisplayName("잔여 포인트보다 많은 포인트를 사용시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenIsAmountLessThanUsePoint() {
        //given
        int point = 4000;
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getAmount), 3500)
                .create();
        when(userReader.getWalletByUserId(wallet.getUserId())).thenReturn(wallet);

        //then //when
        assertThatThrownBy(() -> userCommandService.usePoint(wallet.getUserId(), point))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_ENOUGH_POINT.getMessage());
    }


}