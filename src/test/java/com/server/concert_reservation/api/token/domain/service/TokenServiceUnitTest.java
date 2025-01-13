package com.server.concert_reservation.api.token.domain.service;

import com.server.concert_reservation.api.token.domain.model.dto.TokenInfo;
import com.server.concert_reservation.api.token.domain.model.dto.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.api.token.domain.repository.TokenWriter;
import com.server.concert_reservation.api.token.infrastructure.types.TokenStatus;
import com.server.concert_reservation.common.exception.CustomException;
import com.server.concert_reservation.common.time.TimeManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.Optional;

import static com.server.concert_reservation.api.token.infrastructure.types.TokenStatus.*;
import static com.server.concert_reservation.common.exception.code.TokenErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceUnitTest {

    @Mock
    private TokenReader tokenReader;
    @Mock
    private TokenWriter tokenWriter;
    @Mock
    private TimeManager timeManager;
    @InjectMocks
    private TokenService tokenService;

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

    @DisplayName("대기 상태가 아닌 토큰의 대기번호는 0이다.")
    @ParameterizedTest
    @EnumSource(value = TokenStatus.class, names = {"ACTIVE", "EXPIRED"})
    void notWaitingStatusTokenWaitingNumberIsZero(TokenStatus status) {
        // given
        String token = "token-uuid";
        Long userId = 1L;
        Token waitingToken = Token.builder()
                .userId(userId)
                .token(token)
                .status(status)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);

        // when
        TokenInfo waitingTokenInfo = tokenService.getWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(waitingTokenInfo.waitingNumber(), 0L),
                () -> assertEquals(status, waitingTokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(never()).getLatestActiveToken()
        );
    }

    @DisplayName("최근 활성화된 대기열이 없는 경우 대기번호는 대기열의 id값이다.")
    @Test
    void notWaitingStatusTokenWaitingNumberIsId() {
        // given
        String token = "token-uuid";
        Long userId = 1L;
        Token waitingToken = Token.builder()
                .id(100L)
                .userId(userId)
                .token(token)
                .status(WAITING)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.empty());

        // when
        TokenInfo tokenInfo = tokenService.getWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(100L, tokenInfo.waitingNumber()),
                () -> assertEquals(WAITING, tokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(times(1)).getLatestActiveToken()
        );
    }

    @DisplayName("최근 활성화된 대기열이 없는 경우 대기번호는 화성화된 대기열 id와 조회된 대기열의 id의 차이 값이다.")
    @Test
    void getWaitingTokenTest() {
        // given
        String token = "token-uuid";
        Long userId = 1L;
        Token waitingToken = Token.builder()
                .id(200L)
                .token(token)
                .status(WAITING)
                .build();

        Token latestActivatedQueue = waitingToken.builder()
                .id(150L)
                .token("latest-activated-token")
                .status(ACTIVE)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.of(latestActivatedQueue));

        // when
        TokenInfo tokenInfo = tokenService.getWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(50L, tokenInfo.waitingNumber()),
                () -> assertEquals(WAITING, tokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(times(1)).getLatestActiveToken()
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