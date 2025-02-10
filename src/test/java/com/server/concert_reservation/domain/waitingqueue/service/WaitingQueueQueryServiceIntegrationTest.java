package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueWithPositionInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class WaitingQueueQueryServiceIntegrationTest {

    @Autowired
    private WaitingQueueQueryService waitingQueueQueryService;
    @Autowired
    private WaitingQueueWriter waitingQueueWriter;
    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeEach
    void redisCleansing() {
        redisTemplate.keys("*").forEach(redisTemplate::delete);
    }

    @DisplayName("요청 받은 uuid로 대기열 토큰의 순번을 조회 한다. ")
    @Test
    void shouldGetWaitingTokenWithPositionWhenStatusWaiting() {
        // given
        String uuid = UUID.randomUUID().toString();
        waitingQueueWriter.addWaitingQueue(uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        // when
        WaitingQueueWithPositionInfo result = waitingQueueQueryService.getQueuePosition(uuid);

        // then
        assertEquals(result.uuid(), uuid);
        assertEquals(result.position(), 1);
    }

    @DisplayName("활성화 토큰의 대기열 순번을 조회한다. ")
    @Test
    void shouldGetWaitingTokenWhenStatusActive() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long toEpochSecond = LocalDateTime.now().plus(10, TimeUnit.MINUTES.toChronoUnit())
                .toEpochSecond(ZoneOffset.UTC);
        waitingQueueWriter.addWaitingQueue(uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        waitingQueueWriter.moveToActiveQueue(uuid, toEpochSecond);

        // when
        WaitingQueueWithPositionInfo result = waitingQueueQueryService.getQueuePosition(uuid);

        // then
        assertEquals(result.uuid(), uuid);
        assertEquals(result.position(), 0);
    }

    @DisplayName("존재 하지 않는 토큰으로 대기열 순번 조회시 예외가 발생한다")
    @Test
    void shouldThrownExceptionWhenGetWaitingQueuePositionStatusNotExistActive() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when & then
        assertThatThrownBy(() -> waitingQueueQueryService.getQueuePosition(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(WAITING_QUEUE_NOT_FOUND.getMessage());

    }

    @DisplayName("토큰 활성 여부를 조회한다.")
    @Test
    void shouldSuccessfullyValidateWaitingQueueProcessing() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long toEpochSecond = LocalDateTime.now().plus(10, TimeUnit.MINUTES.toChronoUnit())
                .toEpochSecond(ZoneOffset.UTC);
        waitingQueueWriter.addWaitingQueue(uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        waitingQueueWriter.moveToActiveQueue(uuid, toEpochSecond);

        // when & then
        WaitingQueueInfo waitingQueueInfo = waitingQueueQueryService.validateWaitingQueueProcessing(uuid);

        assertNotNull(waitingQueueInfo);
        assertEquals(uuid, waitingQueueInfo.uuid());
    }

    @DisplayName("만료된 토큰으로 토큰 활성 검증 조회시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenValidateWaitingQueueProcessingWithExpiredToken() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long toEpochSecond = LocalDateTime.now().plus(1, TimeUnit.MILLISECONDS.toChronoUnit())
                .toEpochSecond(ZoneOffset.UTC);
        waitingQueueWriter.addWaitingQueue(uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        waitingQueueWriter.moveToActiveQueue(uuid, toEpochSecond);

        // when && then
        assertThatThrownBy(() -> waitingQueueQueryService.validateWaitingQueueProcessing(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(WAITING_QUEUE_EXPIRED.getMessage());
    }

    @DisplayName("활성 토큰이 아닌 경우 토큰 활성화 검증 조회시 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenValidateWaitingQueueProcessingWithNonActiveToken() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when && then
        assertThatThrownBy(() -> waitingQueueQueryService.validateWaitingQueueProcessing(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(ACTIVE_QUEUE_NOT_FOUND.getMessage());
    }


}
