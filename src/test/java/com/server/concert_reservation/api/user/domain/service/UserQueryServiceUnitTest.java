package com.server.concert_reservation.api.user.domain.service;

import com.server.concert_reservation.api.user.domain.dto.UserInfo;
import com.server.concert_reservation.api.user.domain.dto.WalletInfo;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceUnitTest {
    @Mock
    UserReader userReader;
    @InjectMocks
    UserQueryService userQueryService;

    @DisplayName("유저를 조회한다.")
    @Test
    void getUserTest() {
        //given
        Long id = 1L;
        String name = "test";
        User user = User.of(id, name, LocalDateTime.now(), null);
        when(userReader.getById(id)).thenReturn(user);

        //when
        UserInfo userInfo = userQueryService.getUser(id);

        //then
        assertEquals(userInfo.id(), id);
        assertEquals(userInfo.name(), name);
    }

    @DisplayName("유저를 잔엑을 조회한다.")
    @Test
    void getWalletTest() {
        //given
        Long id = 1L;
        Long userId = 2L;
        int amount = 300;
        Wallet wallet = Wallet.of(id, userId, amount, LocalDateTime.now(), null);

        when(userReader.getWalletByUserId(userId)).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userQueryService.getWallet(userId);

        //then
        assertEquals(updatedWallet.id(), id);
        assertEquals(updatedWallet.userId(), userId);
        assertEquals(updatedWallet.amount(), amount);
    }


}