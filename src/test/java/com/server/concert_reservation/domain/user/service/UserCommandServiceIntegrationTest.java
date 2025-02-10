package com.server.concert_reservation.domain.user.service;

import com.server.concert_reservation.domain.user.UserCommandService;
import com.server.concert_reservation.domain.user.dto.WalletInfo;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.INVALID_POINT;
import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.NOT_ENOUGH_POINT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UserCommandServiceIntegrationTest {

    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserCommandService userCommandService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("사용자가 포인트를 사용한다.")
    @Test
    void usePointTest() {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), savedUser.getId())
                .set(field(Wallet::getAmount), 4000)
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        //when
        WalletInfo walletInfo = userCommandService.usePoint(savedUser.getId(), 3000);

        //then
        assertEquals(walletInfo.amount(), 1000);
    }

    @DisplayName("사용자가 가지고 있는 포인트보다 사용 금액이 많은 경우 예외가 발생한다.")
    @Test
    void isPointLessThanUsePointThrowException() {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), savedUser.getId())
                .set(field(Wallet::getAmount), 1000)
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        // when & then
        assertThatThrownBy(() -> userCommandService.usePoint(savedUser.getId(), 2000))
                .isInstanceOf(CustomException.class)
                .hasMessage(NOT_ENOUGH_POINT.getMessage());
    }

    @DisplayName("사용자가 포인트를 충전한다.")
    @Test
    void chargePointTest() {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), savedUser.getId())
                .set(field(Wallet::getAmount), 1000)
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        //when
        WalletInfo walletInfo = userCommandService.chargePoint(savedUser.getId(), 3000);

        //then
        assertEquals(walletInfo.amount(), 4000);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -500})
    @DisplayName("충전하려는 포인트가 음수인 경우 예외 발생")
    void chargePointNegativeThrowException(int amount) {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), savedUser.getId())
                .set(field(Wallet::getAmount), 1000)
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        //when & then
        assertThatThrownBy(() -> userCommandService.chargePoint(savedUser.getId(), amount))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }


}