package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueWithPositionInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WaitingQueueQueryServiceUnitTest {

    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private TimeManager timeManager;
    @InjectMocks
    private WaitingQueueQueryService waitingQueueQueryService;

    @DisplayName("UUID가 대기열(Waiting Queue)에 있을 경우 1부터 시작하는 순위를 반환한다.")
    @Test
    void getWaitingQueuePosition_WhenUuidInWaitingQueue_ShouldReturnRank() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long rank = 5L;
        when(waitingQueueReader.findRankInWaitingQueue(uuid)).thenReturn(rank);

        // when
        WaitingQueueWithPositionInfo result = waitingQueueQueryService.getWaitingQueuePosition(uuid);

        // then
        assertEquals(rank + 1, result.position());
    }

    @DisplayName("UUID가 활성열(Active Queue)에 있을 경우 0을 반환한다.")
    @Test
    void getWaitingQueuePosition_WhenUuidInActiveQueue_ShouldReturnZero() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(waitingQueueReader.findRankInWaitingQueue(uuid)).thenReturn(null);
        when(waitingQueueReader.findRankInActiveQueue(uuid)).thenReturn(3L);

        // when
        WaitingQueueWithPositionInfo result = waitingQueueQueryService.getWaitingQueuePosition(uuid);

        // then
        assertEquals(result.position(), 0L);
    }

    @DisplayName("UUID가 어느 큐에도 없을 경우 WAITING_QUEUE_NOT_FOUND 예외를 던진다.")
    @Test
    void getWaitingQueuePosition_WhenUuidNotInAnyQueue_ShouldThrowException() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(waitingQueueReader.findRankInWaitingQueue(uuid)).thenReturn(null);
        when(waitingQueueReader.findRankInActiveQueue(uuid)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> waitingQueueQueryService.getWaitingQueuePosition(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(WAITING_QUEUE_NOT_FOUND.getMessage());
    }

    @DisplayName("UUID가 활성열(Active Queue)에 있고, 유효한 경우 WaitingQueueInfo를 반환한다.")
    @Test
    void validateWaitingQueueProcessing_WhenUuidIsValid_ShouldReturnInfo() {
        // given
        String uuid = UUID.randomUUID().toString();
        Double score = (double) (System.currentTimeMillis() / 1000 + 300);
        LocalDateTime expiredAt = LocalDateTime.ofEpochSecond(score.longValue(), 0, ZoneOffset.UTC);

        when(waitingQueueReader.findScoreInActiveQueue(uuid)).thenReturn(score);
        when(timeManager.now()).thenReturn(expiredAt.minusSeconds(100));

        // when
        WaitingQueueInfo result = waitingQueueQueryService.validateWaitingQueueProcessing(uuid);

        // then
        assertEquals(result.uuid(), uuid);
        assertEquals(result.expiredAt(), expiredAt);
    }

    @DisplayName("UUID가 활성열(Active Queue)에 없을 경우 ACTIVE_QUEUE_NOT_FOUND 예외를 던진다.")
    @Test
    void validateWaitingQueueProcessing_WhenUuidNotInActiveQueue_ShouldThrowException() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(waitingQueueReader.findScoreInActiveQueue(uuid)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> waitingQueueQueryService.validateWaitingQueueProcessing(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(ACTIVE_QUEUE_NOT_FOUND.getMessage());
    }

    @DisplayName("UUID가 활성열(Active Queue)에 있지만, 만료된 경우 WAITING_QUEUE_EXPIRED 예외를 던진다.")
    @Test
    void validateWaitingQueueProcessing_WhenUuidIsExpired_ShouldThrowException() {
        // given
        String uuid = UUID.randomUUID().toString();
        Double score = (double) (System.currentTimeMillis() / 1000 - 300);
        LocalDateTime expiredAt = LocalDateTime.ofEpochSecond(score.longValue(), 0, ZoneOffset.UTC);

        when(waitingQueueReader.findScoreInActiveQueue(uuid)).thenReturn(score);
        when(timeManager.now()).thenReturn(expiredAt.plusSeconds(100));

        // when & then
        assertThatThrownBy(() -> waitingQueueQueryService.validateWaitingQueueProcessing(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(WAITING_QUEUE_EXPIRED.getMessage());
    }


}
