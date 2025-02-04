package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.*;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class QueueTokenQueryServiceIntegrationTest {

    @Autowired
    private QueueTokenWriter queueTokenWriter;
    @Autowired
    private QueueTokenQueryService queueTokenQueryService;
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
        QueueToken waitingToken1 = createTokenInstance("token-test1", EXPIRED);
        QueueToken waitingToken2 = createTokenInstance("token-test2", ACTIVE);
        QueueToken waitingToken3 = createTokenInstance("token-test3", WAITING);

        queueTokenWriter.save(waitingToken1);
        queueTokenWriter.save(waitingToken2);
        queueTokenWriter.save(waitingToken3);

        // when
        QueueTokenInfo waitingQueueTokenInfo1 = queueTokenQueryService.findWaitingToken("token-test1", 1L);
        QueueTokenInfo waitingQueueTokenInfo2 = queueTokenQueryService.findWaitingToken("token-test2", 1L);
        QueueTokenInfo waitingQueueTokenInfo3 = queueTokenQueryService.findWaitingToken("token-test3", 1L);

        // then
        assertAll(
                () -> assertEquals(0L, waitingQueueTokenInfo1.waitingNumber()),
                () -> assertEquals(0L, waitingQueueTokenInfo2.waitingNumber()),
                () -> assertEquals(1L, waitingQueueTokenInfo3.waitingNumber())
        );
    }

    @DisplayName("대기열 토큰 대기 순서 조회 성공 - 활성 대기열이 없는 경우")
    @Test
    void getWaitingTokenWhenNotExistActive() {
        // given
        QueueToken waitingToken1 = createTokenInstance("token-test1", WAITING);
        QueueToken waitingToken2 = createTokenInstance("token-test2", WAITING);
        QueueToken waitingToken3 = createTokenInstance("token-test3", WAITING);

        queueTokenWriter.save(waitingToken1);
        queueTokenWriter.save(waitingToken2);
        queueTokenWriter.save(waitingToken3);


        // when
        QueueTokenInfo waitingQueueTokenInfo1 = queueTokenQueryService.findWaitingToken("token-test1", 1L);
        QueueTokenInfo waitingQueueTokenInfo2 = queueTokenQueryService.findWaitingToken("token-test2", 1L);
        QueueTokenInfo waitingQueueTokenInfo3 = queueTokenQueryService.findWaitingToken("token-test3", 1L);

        // then
        assertAll(
                () -> assertEquals(1L, waitingQueueTokenInfo1.waitingNumber()),
                () -> assertEquals(2L, waitingQueueTokenInfo2.waitingNumber()),
                () -> assertEquals(3L, waitingQueueTokenInfo3.waitingNumber())
        );
    }

    @DisplayName("활성화 시키려는 수만큼 현재 대기 상태의 대기열 토큰 조회")
    @Test
    void getWaitingTokenByActiveCount() {
        // given
        QueueToken waitingToken1 = createTokenInstance("token-test1", WAITING);
        QueueToken waitingToken2 = createTokenInstance("token-test2", ACTIVE);
        QueueToken waitingToken3 = createTokenInstance("token-test3", EXPIRED);

        queueTokenWriter.save(waitingToken1);
        queueTokenWriter.save(waitingToken2);
        queueTokenWriter.save(waitingToken3);

        // when
        List<QueueTokenInfo> queueTokenInfoList = queueTokenQueryService.findWaitingTokenByActiveCount(3);

        // then
        assertAll(
                () -> assertEquals(waitingToken1.getToken(), queueTokenInfoList.get(0).token()),
                () -> assertEquals(waitingToken1.getStatus(), queueTokenInfoList.get(0).status())
        );
    }

    @DisplayName("활성화 상태(ACTIVE)의 만료 토큰들을 조회한다.")
    @Test
    void getWaitingTokensToBeExpired() {
        // given
        LocalDateTime now = LocalDateTime.now();
        QueueToken waitingToken = Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getToken), "test-token1")
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getActivatedAt), now.minusMinutes(6))
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();

        queueTokenWriter.save(waitingToken);

        // when
        List<QueueTokenInfo> queueTokenInfoList = queueTokenQueryService.findWaitingTokensToBeExpired(5);

        // then
        assertAll(
                () -> assertEquals(waitingToken.getToken(), queueTokenInfoList.get(0).token()),
                () -> assertEquals(waitingToken.getStatus(), queueTokenInfoList.get(0).status())
        );
    }

    @DisplayName("활성화 시키려는 수만큼 현재 대기 상태의 대기열 토큰 조회")
    @Test
    void getWaitingToken() {
        // given
        QueueToken waitingToken = createTokenInstance("test-token1", WAITING);
        queueTokenWriter.save(waitingToken);

        // when
        QueueTokenInfo queueTokenInfo = queueTokenQueryService.findQueueToken(waitingToken.getToken());

        // then
        assertAll(
                () -> assertEquals(waitingToken.getToken(), queueTokenInfo.token()),
                () -> assertEquals(waitingToken.getStatus(), queueTokenInfo.status())
        );
    }

    private QueueToken createTokenInstance(String token, QueueTokenStatus status) {
        return Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getToken), token)
                .set(field(QueueToken::getUserId), 1L)
                .set(field(QueueToken::getStatus), status)
                .create();
    }
}
