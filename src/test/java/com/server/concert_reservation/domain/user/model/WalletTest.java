package com.server.concert_reservation.domain.user.model;

import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.NOT_ENOUGH_POINT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.Assert.assertEquals;

class WalletTest {

    @DisplayName("유저의 포인트를 충전한다.")
    @Test
    void chargePointTest() {
        //given
        Long userId = 1L;
        int totalPrice = 3000;
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getUserId), userId)
                .set(field(Wallet::getAmount), totalPrice)
                .create();

        //when
        wallet.chargeAmount(1000);

        //then
        assertEquals(wallet.getUserId(), userId);
        assertEquals(wallet.getAmount(), 4000);
    }

    @DisplayName("0원이거나 0원 보다 낮은 포인트 입력시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenZeroOrMinusPoint() {
        //given
        Wallet wallet = Instancio.create(Wallet.class);

        //then //when
        assertThatThrownBy(() -> wallet.chargeAmount(0))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }

    @DisplayName("유저의 포인트를 사용한다.")
    @Test
    void usePointTest() {
        //given
        int point = 1000;
        Long userId = 2L;
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getUserId), userId)
                .set(field(Wallet::getAmount), 2000)
                .create();

        //when
        wallet.useAmount(point);

        //then
        assertEquals(wallet.getUserId(), userId);
        assertEquals(wallet.getAmount(), 1000);
    }


    @DisplayName("잔여 포인트보다 많은 포인트를 사용시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenIsAmountLessThanUsePoint() {
        //given
        Wallet wallet = Instancio.of(Wallet.class)
                .set(field(Wallet::getAmount), 2000)
                .create();

        //then //when
        assertThatThrownBy(() -> wallet.useAmount(5000))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_ENOUGH_POINT.getMessage());
    }


}