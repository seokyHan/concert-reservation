package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.*;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueTokenCommandServiceUnitTest {

    @Mock
    private QueueTokenReader tokenReader;
    @Mock
    private QueueTokenWriter tokenWriter;
    @Mock
    private TimeManager timeManager;
    @Mock
    private UUIDManager uuidManager;
    @InjectMocks
    private QueueTokenCommandService tokenService;

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createTokenTest() {
        // given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), uuidManager.generateUuid())
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        when(tokenWriter.save(any(QueueToken.class))).thenReturn(queueToken);


        // when
        QueueTokenInfo waitingToken = tokenService.createToken(queueToken.getUserId());

        // then
        assertAll(
                () -> assertEquals(queueToken.getUserId(), waitingToken.userId()),
                () -> assertEquals(queueToken.getToken(), waitingToken.token()),
                () -> assertEquals(queueToken.getStatus(), waitingToken.status()),
                () -> then(tokenWriter).should(times(1)).save(any(QueueToken.class))
        );
    }


    @DisplayName("활성화된 대기열 토큰의 경우 예외가 발생하지 않는다.")
    @Test
    void activeTokenNotThrowException() {
        // given
        String token = "token-uuid";
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);

        // when & then
        assertDoesNotThrow(() -> tokenService.checkActivatedToken(token));
        then(tokenReader).should(times(1)).getByToken(token);

    }

    @DisplayName("만료된 대기열 토큰의 경우 예외가 발생한다.")
    @Test
    void expiredTokenThrowException() {
        // given
        String token = "token-uuid";
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), EXPIRED)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);

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
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);


        // when & then
        assertThatThrownBy(() -> tokenService.checkActivatedToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_NOT_ACTIVATED.getMessage());
    }

    @DisplayName("대기열 활성화 테스트")
    @Test
    void activateTokenTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String token = "token-uuid";

        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);
        when(timeManager.now()).thenReturn(now);

        // when
        tokenService.activateToken(token);

        // then
        assertAll(
                () -> assertEquals(ACTIVE, queueToken.getStatus()),
                () -> assertEquals(now, queueToken.getActivatedAt()),
                () -> then(tokenWriter).should(times(1)).save(queueToken)
        );
    }

    @DisplayName("이미 활성화된 대기열 토큰을 활성화 시키면 예외가 발생한다")
    @Test
    void alreadyActivateTokenThrowsException() {
        // given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), "test-token")
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();

        // when & then
        assertThatThrownBy(() -> queueToken.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_ACTIVATED.getMessage());
    }

    @DisplayName("만료된 대기열을 활성화 시키는 경우 예외가 발생한다.")
    @Test
    void expiredTokenWhenActivatedThrowsException() {
        // given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), "test-token")
                .set(field(QueueToken::getStatus), EXPIRED)
                .create();

        // when & then
        assertThatThrownBy(() -> queueToken.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_EXPIRED.getMessage());
    }

    @DisplayName("대기열 토큰 만료 테스트")
    @Test
    void expireTokenTest() {
        // given
        String token = "test-token";
        LocalDateTime now = LocalDateTime.now();

        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getToken), "test-token")
                .set(field(QueueToken::getStatus), EXPIRED)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);
        when(timeManager.now()).thenReturn(now);


        // when
        tokenService.expireToken(token);

        // then
        assertAll(
                () -> assertEquals(EXPIRED, queueToken.getStatus()),
                () -> assertEquals(now, queueToken.getExpiredAt()),
                () -> then(tokenWriter).should(times(1)).save(queueToken)
        );
    }

}