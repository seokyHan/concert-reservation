package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api_backup.token.application.TokenQueryUseCase;
import com.server.concert_reservation.api_backup.token.application.dto.TokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TokenQueryServiceIntegrationTest {

    @Autowired
    private QueueTokenWriter tokenWriter;
    @Autowired
    private TokenQueryUseCase tokenQueryUseCase;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("대기열 토큰 대기 순서 조회 성공 - 대기열상태가 WAITING인 경우만 순서(id) 반환")
    @Test
    void getWaitingTokenWhenStatusWaiting() {
        // given

        QueueToken waitingToken1 = QueueToken.builder()
                .token("token-test1")
                .userId(1L)
                .status(EXPIRED)
                .build();

        QueueToken waitingToken2 = QueueToken.builder()
                .token("token-test2")
                .userId(1L)
                .status(ACTIVE)
                .build();

        QueueToken waitingToken3 = QueueToken.builder()
                .token("token-test3")
                .userId(1L)
                .status(WAITING)
                .build();

        tokenWriter.save(waitingToken1);
        tokenWriter.save(waitingToken2);
        tokenWriter.save(waitingToken3);

        // when
        TokenInfo waitingTokenInfo1 = tokenQueryUseCase.getWaitingToken("token-test1", 1L);
        TokenInfo waitingTokenInfo2 = tokenQueryUseCase.getWaitingToken("token-test2", 1L);
        TokenInfo waitingTokenInfo3 = tokenQueryUseCase.getWaitingToken("token-test3", 1L);

        // then
        assertAll(
                () -> assertEquals(0L, waitingTokenInfo1.waitingNumber()),
                () -> assertEquals(0L, waitingTokenInfo2.waitingNumber()),
                () -> assertEquals(1L, waitingTokenInfo3.waitingNumber())
        );
    }

    @DisplayName("대기열 토큰 대기 순서 조회 성공 - 활성 대기열이 없는 경우")
    @Test
    void getWaitingTokenWhenNotExistActive() {
        // given

        QueueToken waitingToken1 = QueueToken.builder()
                .token("token-test1")
                .userId(1L)
                .status(WAITING)
                .build();

        QueueToken waitingToken2 = QueueToken.builder()
                .token("token-test2")
                .userId(1L)
                .status(WAITING)
                .build();

        QueueToken waitingToken3 = QueueToken.builder()
                .token("token-test3")
                .userId(1L)
                .status(WAITING)
                .build();

        tokenWriter.save(waitingToken1);
        tokenWriter.save(waitingToken2);
        tokenWriter.save(waitingToken3);

        // when
        TokenInfo waitingTokenInfo1 = tokenQueryUseCase.getWaitingToken("token-test1", 1L);
        TokenInfo waitingTokenInfo2 = tokenQueryUseCase.getWaitingToken("token-test2", 1L);
        TokenInfo waitingTokenInfo3 = tokenQueryUseCase.getWaitingToken("token-test3", 1L);

        // then
        assertAll(
                () -> assertEquals(1L, waitingTokenInfo1.waitingNumber()),
                () -> assertEquals(2L, waitingTokenInfo2.waitingNumber()),
                () -> assertEquals(3L, waitingTokenInfo3.waitingNumber())
        );
    }
}
