package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueTokenCommandServiceUnitTest {

    @InjectMocks
    private WaitingQueueCommandService waitingQueueCommandService;
    @Mock
    private WaitingQueueWriter waitingQueueWriter;
    @Mock
    private UUIDManager uuidManager;

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createWaitingTokenShouldReturnGeneratedUuid() {
        // given
        String generatedUuid = UUID.randomUUID().toString();
        when(uuidManager.generateUuid()).thenReturn(generatedUuid);
        when(waitingQueueWriter.saveWaitingQueue(generatedUuid)).thenReturn(generatedUuid);

        // when
        String result = waitingQueueCommandService.createWaitingToken();

        // then
        assertEquals(generatedUuid, result);
        verify(uuidManager).generateUuid();
        verify(waitingQueueWriter).saveWaitingQueue(generatedUuid);
    }

    @DisplayName("대기열 토큰을 활성화 실행을 검증한다.")
    @Test
    void activateWaitingQueueShouldProveWaitingQueueWriter() {
        // given
        int availableSlots = 5;
        int timeout = 10;

        // when
        waitingQueueCommandService.activateWaitingQueue(availableSlots, timeout);

        // then
        verify(waitingQueueWriter).activateWaitingQueues(availableSlots, timeout, TimeUnit.MINUTES);
    }

    @DisplayName("입력 받은 토큰으로 대기열 삭제 호출을 검증한다.")
    @Test
    void removeActiveQueueByUuidShouldProveDeleteActiveTokenByUuid() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when
        waitingQueueCommandService.removeActiveQueueByUuid(uuid);

        // then
        verify(waitingQueueWriter).deleteActiveTokenByUuid(uuid);
    }

    @DisplayName("대기열 토큰 삭제 호출을 검증한다.")
    @Test
    void removeActiveQueueShouldProveDeleteActiveToken() {
        // when
        waitingQueueCommandService.removeActiveQueue();

        // then
        verify(waitingQueueWriter).deleteActiveToken();
    }

}