package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueueTokenQueryServiceUnitTest {

    @Mock
    private QueueTokenReader tokenReader;
    @InjectMocks
    private QueueTokenQueryService tokenQueryService;

    @DisplayName("대기 상태가 아닌 토큰의 대기번호는 0이다.")
    @ParameterizedTest
    @EnumSource(value = QueueTokenStatus.class, names = {"ACTIVE", "EXPIRED"})
    void notWaitingStatusTokenWaitingNumberIsZero(QueueTokenStatus status) {
        // given
        String token = "token-uuid";
        Long userId = 1L;

        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getUserId), userId)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), status)
                .create();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(queueToken);

        // when
        QueueTokenInfo waitingQueueTokenInfo = tokenQueryService.findWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(waitingQueueTokenInfo.waitingNumber(), 0L),
                () -> assertEquals(status, waitingQueueTokenInfo.status()),
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
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getId), 100L)
                .set(field(QueueToken::getUserId), userId)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(queueToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.empty());

        // when
        QueueTokenInfo queueTokenInfo = tokenQueryService.findWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(queueToken.getId(), queueTokenInfo.waitingNumber()),
                () -> assertEquals(queueToken.getStatus(), queueTokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(times(1)).getLatestActiveToken()
        );
    }

    @DisplayName("최근 활성화된 대기열이 없는 경우 대기번호는 화성화된 대기열 id와 조회된 대기열의 id의 차이 값이다.")
    @Test
    void waitingTokenDifferenceWhenNoActiveQueue() {
        // given
        String token = "token-uuid";
        Long userId = 1L;
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getId), 200L)
                .set(field(QueueToken::getUserId), userId)
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getStatus), WAITING)
                .create();

        QueueToken latestActivatedQueue = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getId), 150L)
                .set(field(QueueToken::getToken), "latest-activated-token")
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(queueToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.of(latestActivatedQueue));

        // when
        QueueTokenInfo queueTokenInfo = tokenQueryService.findWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(50L, queueTokenInfo.waitingNumber()),
                () -> assertEquals(WAITING, queueTokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(times(1)).getLatestActiveToken()
        );
    }

    @DisplayName("토큰 만료시간이 지난 토큰들을 조회한다.")
    @Test
    void getWaitingTokenToBeExpired() {
        // given
        QueueToken queueToken = Instancio.create(QueueToken.class);
        when(tokenReader.getWaitingTokensToBeExpired(5)).thenReturn(List.of(queueToken));

        // when
        List<QueueTokenInfo> queueTokenInfos = tokenQueryService.findWaitingTokensToBeExpired(5);

        // then
        assertAll(
                () -> assertEquals(queueToken.getId(), queueTokenInfos.get(0).id()),
                () -> assertEquals(queueToken.getStatus(), queueTokenInfos.get(0).status())
        );
    }

    @DisplayName("전달받은 token 으로 토큰 인스턴스를 조회한다.")
    @Test
    void getWaitingTokenByInputToken() {
        // given
        String token = "token-uuid";
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getToken), token)
                .create();
        when(tokenReader.getByToken(token)).thenReturn(queueToken);

        // when
        QueueTokenInfo queueTokenInfo = tokenQueryService.findQueueToken(token);

        // then
        assertAll(
                () -> assertEquals(queueToken.getId(), queueTokenInfo.id()),
                () -> assertEquals(queueToken.getToken(), queueTokenInfo.token()),
                () -> assertEquals(queueToken.getStatus(), queueTokenInfo.status())
        );
    }
}
