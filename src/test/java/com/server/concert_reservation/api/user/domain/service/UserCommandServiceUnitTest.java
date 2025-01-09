package com.server.concert_reservation.api.user.domain.service;

import com.server.concert_reservation.api.user.domain.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.dto.command.UserCommand;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.entity.WalletEntity;
import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.server.concert_reservation.common.exception.code.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.common.exception.code.UserErrorCode.NOT_ENOUGH_POINT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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
        Long id = 1L;
        Long userId = 2L;
        int point = 3000;

        Wallet wallet = Wallet.of(id, userId, 500, LocalDateTime.now(), null);
        UserCommand userCommand = new UserCommand(userId, point);

        when(userReader.getWalletByUserId(userId)).thenReturn(wallet);
        when(userWriter.saveUserPoint(any(WalletEntity.class))).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userCommandService.chargePoint(userCommand);

        //then
        assertEquals(updatedWallet.id(), id);
        assertEquals(updatedWallet.userId(), userId);
        assertEquals(updatedWallet.amount(), 3500);
    }

    @DisplayName("유저의 포인트를 사용한다.")
    @Test
    void useUserPoint() {
        //given
        Long id = 1L;
        Long userId = 2L;
        int point = 3000;

        Wallet wallet = Wallet.of(id, userId, 3500, LocalDateTime.now(), null);
        UserCommand userCommand = new UserCommand(userId, point);

        when(userReader.getWalletByUserId(userId)).thenReturn(wallet);
        when(userWriter.saveUserPoint(any(WalletEntity.class))).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userCommandService.usePoint(userCommand);

        //then
        assertEquals(updatedWallet.id(), id);
        assertEquals(updatedWallet.userId(), userId);
        assertEquals(updatedWallet.amount(), 500);
    }

    @DisplayName("충전 포인트가 0원이거나 0원보다 작은 경우 예외가 발생한다. .")
    @Test
    void shouldThrowExceptionWhenZeroOrMinusPoint() {
        //given
        Long id = 1L;
        Long userId = 2L;
        int point = 0;

        Wallet wallet = Wallet.of(id, userId, 3500, LocalDateTime.now(), null);
        UserCommand userCommand = new UserCommand(userId, point);

        when(userReader.getWalletByUserId(userId)).thenReturn(wallet);

        //then //when
        assertThatThrownBy(() -> userCommandService.chargePoint(userCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }

    @DisplayName("잔여 포인트보다 많은 포인트를 사용시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenIsAmountLessThanUsePoint() {
        //given
        Long id = 1L;
        Long userId = 2L;
        int point = 4000;

        Wallet wallet = Wallet.of(id, userId, 3500, LocalDateTime.now(), null);
        UserCommand userCommand = new UserCommand(userId, point);

        when(userReader.getWalletByUserId(userId)).thenReturn(wallet);

        //then //when
        assertThatThrownBy(() -> userCommandService.usePoint(userCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_ENOUGH_POINT.getMessage());
    }



}