package com.server.concert_reservation.api.user.domain.model;

import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.server.concert_reservation.common.exception.code.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.common.exception.code.UserErrorCode.NOT_ENOUGH_POINT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

class WalletTest {

    @DisplayName("유저의 포인트를 충전한다.")
    @Test
    void ChargePointTest() {
        //given
        int point = 1000;
        Long id = 1L;
        Long userId = 2L;
        Wallet wallet = Wallet.of(id, userId, 3000, LocalDateTime.now(), null);

        //when
        wallet.chargeAmount(point);

        //then
        assertEquals(wallet.getId(), id);
        assertEquals(wallet.getUserId(), userId);
        assertEquals(wallet.getAmount(), 4000);
    }

    @DisplayName("0원이거나 0원 보다 낮은 포인트 입력시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenZeroOrMinusPoint() {
        //given
        int point = 0;
        Wallet wallet = Wallet.of(1L, 1L, 3000, LocalDateTime.now(), null);

        //then //when
        assertThatThrownBy(() -> wallet.chargeAmount(point))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }

    @DisplayName("유저의 포인트를 사용한다.")
    @Test
    void UsePointTest() {
        //given
        int point = 1000;
        Long id = 1L;
        Long userId = 2L;
        Wallet wallet = Wallet.of(id, userId, 3000, LocalDateTime.now(), null);

        //when
        wallet.useAmount(point);

        //then
        assertEquals(wallet.getId(), id);
        assertEquals(wallet.getUserId(), userId);
        assertEquals(wallet.getAmount(), 2000);
    }


    @DisplayName("잔여 포인트보다 많은 포인트를 사용시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenIsAmountLessThanUsePoint() {
        //given
        int point = 5000;
        Wallet wallet = Wallet.of(1L, 1L, 3000, LocalDateTime.now(), null);

        //then //when
        assertThatThrownBy(() -> wallet.useAmount(point))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_ENOUGH_POINT.getMessage());
    }


}