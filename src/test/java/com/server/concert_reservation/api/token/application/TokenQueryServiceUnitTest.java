package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api_backup.token.application.TokenQueryService;
import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenQueryServiceUnitTest {

    @Mock
    private QueueTokenReader tokenReader;
    @InjectMocks
    private TokenQueryService tokenQueryService;

    @DisplayName("대기 상태가 아닌 토큰의 대기번호는 0이다.")
    @ParameterizedTest
    @EnumSource(value = QueueTokenStatus.class, names = {"ACTIVE", "EXPIRED"})
    void notWaitingStatusTokenWaitingNumberIsZero(QueueTokenStatus status) {
        // given
        String token = "token-uuid";
        Long userId = 1L;
        QueueToken waitingToken = QueueToken.builder()
                .userId(userId)
                .token(token)
                .status(status)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);

        // when
        TokenInfo waitingTokenInfo = tokenQueryService.getWaitingToken(token, userId);

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
        QueueToken waitingToken = QueueToken.builder()
                .id(100L)
                .userId(userId)
                .token(token)
                .status(WAITING)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.empty());

        // when
        TokenInfo tokenInfo = tokenQueryService.getWaitingToken(token, userId);

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
        QueueToken waitingToken = QueueToken.builder()
                .id(200L)
                .token(token)
                .status(WAITING)
                .build();

        QueueToken latestActivatedQueue = waitingToken.builder()
                .id(150L)
                .token("latest-activated-token")
                .status(ACTIVE)
                .build();
        when(tokenReader.getByUserIdAndToken(userId, token)).thenReturn(waitingToken);
        when(tokenReader.getLatestActiveToken()).thenReturn(Optional.of(latestActivatedQueue));

        // when
        TokenInfo tokenInfo = tokenQueryService.getWaitingToken(token, userId);

        // then
        assertAll(
                () -> assertEquals(50L, tokenInfo.waitingNumber()),
                () -> assertEquals(WAITING, tokenInfo.status()),
                () -> then(tokenReader).should(times(1)).getByUserIdAndToken(userId, token),
                () -> then(tokenReader).should(times(1)).getLatestActiveToken()
        );
    }
}
