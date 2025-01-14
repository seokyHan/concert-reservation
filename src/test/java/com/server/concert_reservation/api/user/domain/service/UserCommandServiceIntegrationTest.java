package com.server.concert_reservation.api.user.domain.service;

import com.server.concert_reservation.api.user.application.UserQueryUseCase;
import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import com.server.concert_reservation.api.user.infrastructure.repository.WalletJpaRepository;
import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.server.concert_reservation.common.exception.code.UserErrorCode.INVALID_POINT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCommandServiceIntegrationTest {

    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserReader userReader;
    @Autowired
    private UserQueryUseCase pointUseCase;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @BeforeEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        walletJpaRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 포인트를 사용한다.")
    @Test
    void usePointTest() {
        //given
        Long userId = 1L;
        int point = 3000;

        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(4000)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand userCommand = new UserCommand(userId, point);

        //when
        pointUseCase.usePoint(userCommand);
        Wallet userPoint = userReader.getWalletByUserId(userId);

        //then
        assertEquals(userPoint.getAmount(), 1000);
    }

    @DisplayName("사용자가 가지고 있는 포인트보다 사용 금액이 많은 경우 예외가 발생한다.")
    @Test
    void isPointLessThanUsePointThrowException() {
        //given
        Long userId = 1L;
        int point = 3000;

        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(1000)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand userCommand = new UserCommand(userId, point);

        // when & then
        assertThatThrownBy(() -> pointUseCase.usePoint(userCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }

    @DisplayName("사용자가 포인트를 충전한다.")
    @Test
    void chargePointTest() {
        //given
        Long userId = 1L;
        int point = 3000;

        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(point)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand userCommand = new UserCommand(userId, point);

        //when
        pointUseCase.chargePoint(userCommand);
        Wallet userPoint = userReader.getWalletByUserId(userId);

        //then
        assertEquals(userPoint.getAmount(), 6000);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -500})
    @DisplayName("충전하려는 포인트가 음수인 경우 예외 발생")
    void chargePointNegativeThrowException(int amount) {
        //given
        Long userId = 1L;
        int point = 3000;

        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(point)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand userCommand = new UserCommand(userId, amount);

        //when & then
        assertThatThrownBy(() -> pointUseCase.chargePoint(userCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(INVALID_POINT.getMessage());
    }


}