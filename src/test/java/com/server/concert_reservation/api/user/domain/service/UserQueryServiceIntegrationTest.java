package com.server.concert_reservation.api.user.domain.service;

import com.server.concert_reservation.api.user.application.UserCommandUseCase;
import com.server.concert_reservation.api.user.application.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import com.server.concert_reservation.api.user.infrastructure.repository.WalletJpaRepository;
import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.server.concert_reservation.common.exception.code.UserErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserQueryServiceIntegrationTest {

    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserCommandUseCase getUserUseCase;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WalletJpaRepository walletJpaRepository;

    @BeforeEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        walletJpaRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 포인트를 조회한다.")
    @Test
    void getWalletTest() {
        //given
        Long userId = 1L;
        int amount = 3000;

        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(amount)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        //when
        WalletInfo walletInfo = getUserUseCase.getWallet(userId);

        //then
        assertEquals(amount, walletInfo.amount());
        assertEquals(userId, walletInfo.userId());
    }

    @DisplayName("사용자 정보가 없는 경우 예외 발생.")
    @Test
    void userNotFoundThrowException() {
        //when & then
        assertThatThrownBy(() -> getUserUseCase.getUser(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

}