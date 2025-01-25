package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api_backup.token.application.TokenCommandService;
import com.server.concert_reservation.api_backup.token.application.dto.TokenCommand;
import com.server.concert_reservation.domain.queue_token.model.Token;
import com.server.concert_reservation.domain.queue_token.repository.TokenReader;
import com.server.concert_reservation.domain.queue_token.repository.TokenWriter;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenCommandServiceUnitTest {

    @Mock
    private TokenReader tokenReader;
    @Mock
    private TokenWriter tokenWriter;
    @Mock
    private TimeManager timeManager;
    @InjectMocks
    private TokenCommandService tokenService;

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createTokenTest() {
        // given
        Long userId = 1L;
        String token = "token-uuid";

        Token expectedWaitingToken = new Token(userId, token);
        when(tokenWriter.save(any(Token.class))).thenReturn(expectedWaitingToken);

        TokenCommand command = new TokenCommand(userId, token);

        // when
        Token waitingToken = tokenService.createToken(command);

        // then
        assertAll(
                () -> assertEquals(userId, waitingToken.getUserId()),
                () -> assertEquals(token, waitingToken.getToken()),
                () -> assertEquals(WAITING, waitingToken.getStatus()),
                () -> then(tokenWriter).should(times(1)).save(any(Token.class))
        );
    }


    @DisplayName("활성화된 대기열 토큰의 경우 예외가 발생하지 않는다.")
    @Test
    void activeTokenNotThrowExceptionTest() {
        // given
        final String token = "token-uuid";
        Long userId = 200L;

        Token waitingToken = Token.builder()
                .id(userId)
                .token(token)
                .status(ACTIVE)
                .build();

        when(tokenReader.getByToken(token)).thenReturn(waitingToken);

        // when & then
        assertDoesNotThrow(() -> tokenService.checkActivatedToken(token));
        then(tokenReader).should(times(1)).getByToken(token);

    }

    @DisplayName("만료된 대기열 토큰의 경우 예외가 발생한다.")
    @Test
    void expiredTokenThrowExceptionTest() {
        // given
        String token = "token-uuid";
        Long userId = 200L;

        Token waitingToken = Token.builder()
                .id(userId)
                .token(token)
                .status(EXPIRED)
                .build();

        when(tokenReader.getByToken(token)).thenReturn(waitingToken);

        // when & then
        assertThatThrownBy(() -> tokenService.checkActivatedToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_EXPIRED.getMessage());

    }

    @DisplayName("활성화 되지 않은 대기열 토큰은 예외를 발생시킨다.")
    @Test
    void notActiveTokenThrowsException() {
        // given
        String token = "token-uuid";
        Long userId = 200L;

        Token waitingToken = Token.builder()
                .id(userId)
                .token(token)
                .status(WAITING)
                .build();

        when(tokenReader.getByToken(token)).thenReturn(waitingToken);

        // when & then
        assertThatThrownBy(() -> tokenService.checkActivatedToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_NOT_ACTIVATED.getMessage());
    }

    @DisplayName("대기열 활성화 테스트")
    @Test
    void activateTokenTest() {
        // given
        String token = "test-token";
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Token waitingToken = Token.builder()
                .id(userId)
                .token(token)
                .status(WAITING)
                .build();

        when(tokenReader.getByToken(token)).thenReturn(waitingToken);
        when(timeManager.now()).thenReturn(now);

        // when
        tokenService.activateToken(token);

        // then
        assertAll(
                () -> assertEquals(ACTIVE, waitingToken.getStatus()),
                () -> assertEquals(now, waitingToken.getActivatedAt()),
                () -> then(tokenWriter).should(times(1)).save(waitingToken)
        );
    }

    @DisplayName("이미 활성화된 대기열 토큰을 활성화 시키면 예외가 발생한다")
    @Test
    void alreadyActivateTokenThrowsException() {
        // given
        Token waitingToken = Token.builder()
                .id(1L)
                .token("test-token")
                .status(ACTIVE)
                .build();

        // when & then
        assertThatThrownBy(() -> waitingToken.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_ACTIVATED.getMessage());
    }

    @DisplayName("만료된 대기열을 활성화 시키는 경우 예외가 발생한다.")
    @Test
    void expiredTokenWhenActivatedThrowsException() {
        // given
        Token waitingToken = Token.builder()
                .id(1L)
                .token("test-token")
                .status(EXPIRED)
                .build();

        // when & then
        assertThatThrownBy(() -> waitingToken.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_EXPIRED.getMessage());
    }


    @DisplayName("대기열 토큰 만료 테스트")
    @Test
    void expireTokenTest() {
        // given
        String token = "test-token";
        LocalDateTime now = LocalDateTime.now();

        // Mock 대기열 생성
        Token waitingToken = Token.builder()
                .id(1L)
                .token(token)
                .status(ACTIVE)
                .build();

        when(tokenReader.getByToken(token)).thenReturn(waitingToken);
        when(timeManager.now()).thenReturn(now);


        // when
        tokenService.expireToken(token);

        // then
        assertAll(
                () -> assertEquals(EXPIRED, waitingToken.getStatus()),
                () -> assertEquals(now, waitingToken.getExpiredAt()),
                () -> then(tokenWriter).should(times(1)).save(waitingToken)
        );
    }

}