package com.server.concert_reservation.domain.user.service;

import com.server.concert_reservation.domain.user.UserQueryService;
import com.server.concert_reservation.domain.user.dto.UserInfo;
import com.server.concert_reservation.domain.user.dto.WalletInfo;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserReader;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        User user = Instancio.create(User.class);
        when(userReader.getById(user.getId())).thenReturn(user);

        //when
        UserInfo userInfo = userQueryService.findUser(user.getId());

        //then
        assertEquals(userInfo.id(), user.getId());
        assertEquals(userInfo.name(), user.getName());
    }

    @DisplayName("유저를 잔엑을 조회한다.")
    @Test
    void getWalletTest() {
        //given
        Wallet wallet = Instancio.create(Wallet.class);
        when(userReader.getWalletByUserId(wallet.getUserId())).thenReturn(wallet);

        //when
        WalletInfo updatedWallet = userQueryService.findWallet(wallet.getUserId());

        //then
        assertEquals(updatedWallet.id(), wallet.getId());
        assertEquals(updatedWallet.userId(), wallet.getUserId());
        assertEquals(updatedWallet.amount(), wallet.getAmount());
    }


}