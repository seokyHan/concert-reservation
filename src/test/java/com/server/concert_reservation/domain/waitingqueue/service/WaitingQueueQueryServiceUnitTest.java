package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;
import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueWithPositionInfo;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.WAITING_QUEUE_EXPIRED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WaitingQueueQueryServiceUnitTest {

    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private TimeManager timeManager;
    @InjectMocks
    private WaitingQueueQueryService waitingQueueQueryService;

    @DisplayName("요청 받은 uuid로 대기열 토큰의 순번을 조회한다")
    @Test
    void requestUuidGetWaitingQueuePosition() {
        // given
        String uuid = UUID.randomUUID().toString();
        Long position = 1L;
        when(waitingQueueReader.findWaitingQueuePosition(uuid)).thenReturn(position);

        // when
        WaitingQueueWithPositionInfo waitingQueueWithPositionInfo = waitingQueueQueryService.getWaitingQueuePosition(uuid);

        // then
        assertAll(
                () -> assertEquals(waitingQueueWithPositionInfo.position(), position),
                () -> assertEquals(waitingQueueWithPositionInfo.uuid(), uuid),
                () -> then(waitingQueueReader).should(times(1)).findWaitingQueuePosition(uuid)
        );
    }

    @DisplayName("만료시간 보다 현재 시간이 앞서 있는 경우 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenCurrentTimeIsBeforeExpirationTime() {
        //given
        String uuid = UUID.randomUUID().toString();
        WaitingQueueInfo waitingQueueInfo = Instancio.of(WaitingQueueInfo.class)
                .set(field(WaitingQueueInfo::expiredAt), LocalDateTime.now().minusMinutes(1L))
                .create();
        when(timeManager.now()).thenReturn(LocalDateTime.now());
        when(waitingQueueReader.findActiveToken(uuid)).thenReturn(waitingQueueInfo);

        //when & then
        assertThatThrownBy(() -> waitingQueueQueryService.validateWaitingQueueProcessing(uuid))
                .isInstanceOf(CustomException.class)
                .hasMessage(WAITING_QUEUE_EXPIRED.getMessage());
    }


    @DisplayName("현재 활성화 중인 대기열 토큰을 조회한다.")
    @Test
    void getPresentActiveWaitingQueue() {
        // given
        String uuid = UUID.randomUUID().toString();
        WaitingQueueInfo waitingQueueInfo = Instancio.of(WaitingQueueInfo.class)
                .set(field(WaitingQueueInfo::expiredAt), LocalDateTime.now().plusMinutes(1L))
                .create();
        when(waitingQueueReader.findActiveToken(uuid)).thenReturn(waitingQueueInfo);

        // when
        WaitingQueueInfo queueTokenInfo = waitingQueueReader.findActiveToken(uuid);

        // then
        assertAll(
                () -> assertEquals(queueTokenInfo.uuid(), waitingQueueInfo.uuid()),
                () -> assertEquals(queueTokenInfo.expiredAt(), waitingQueueInfo.expiredAt()),
                () -> then(waitingQueueReader).should(times(1)).findActiveToken(uuid)
        );
    }


}
