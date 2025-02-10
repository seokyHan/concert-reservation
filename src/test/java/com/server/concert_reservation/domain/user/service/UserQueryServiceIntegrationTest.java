package com.server.concert_reservation.domain.user.service;

import com.server.concert_reservation.domain.user.UserQueryService;
import com.server.concert_reservation.domain.user.dto.UserInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.server.concert_reservation.domain.user.errorcode.UserErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserQueryServiceIntegrationTest {

    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserQueryService userQueryService;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("사용자 포인트를 조회한다.")
    @Test
    void getWalletTest() {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), user.getId())
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        //when
        WalletInfo walletInfo = userQueryService.findWallet(user.getId());

        //then
        assertEquals(wallet.getAmount(), walletInfo.amount());
        assertEquals(user.getId(), walletInfo.userId());
    }

    @DisplayName("사용자 정보가 없는 경우 예외 발생.")
    @Test
    void userNotFoundThrowException() {
        //when & then
        assertThatThrownBy(() -> userQueryService.findUser(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @DisplayName("유저를 조회한다.")
    @Test
    void getUserTest() {
        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        //when
        UserInfo userInfo = userQueryService.findUser(savedUser.getId());

        //then
        assertEquals(userInfo.id(), savedUser.getId());
        assertEquals(userInfo.name(), user.getName());
    }

}