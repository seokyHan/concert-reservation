package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueReader;
import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WaitingQueueCommandServiceUnitTest {

    @InjectMocks
    private WaitingQueueCommandService waitingQueueCommandService;
    @Mock
    private WaitingQueueWriter waitingQueueWriter;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private UUIDManager uuidManager;

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createWaitingTokenShouldReturnGeneratedUuid() {
        // given
        String generatedUuid = UUID.randomUUID().toString();
        Long toEpochSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        when(uuidManager.generateUuid()).thenReturn(generatedUuid);
        when(waitingQueueWriter.addWaitingQueue(generatedUuid, toEpochSecond)).thenReturn(true);

        // when
        String result = waitingQueueCommandService.createWaitingToken();

        // then
        assertEquals(generatedUuid, result);
        verify(uuidManager).generateUuid();
        verify(waitingQueueWriter).addWaitingQueue(generatedUuid, toEpochSecond);
    }

    @DisplayName("대기열 토큰을 활성화 실행을 검증한다.")
    @Test
    void activateWaitingQueueShouldProveWaitingQueueWriter() {
        // given
        int availableSlots = 5;
        int timeout = 10;
        String generatedUuid = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Set<Object> set = Set.of(generatedUuid);
        when(waitingQueueReader.getWaitingQueue(availableSlots)).thenReturn(set);

        // when
        waitingQueueCommandService.activateWaitingQueue(availableSlots, timeout);
        ArgumentCaptor<Long> expirationCaptor = ArgumentCaptor.forClass(Long.class);
        verify(waitingQueueWriter).moveToActiveQueue(eq(generatedUuid), expirationCaptor.capture());
        Long capturedExpiration = expirationCaptor.getValue();
        Long expectedExpiration = now.plusMinutes(timeout).toEpochSecond(ZoneOffset.UTC);

        // then
        assertThat(capturedExpiration).isBetween(expectedExpiration - 1, expectedExpiration + 1);
    }

    @DisplayName("입력 받은 토큰으로 대기열 삭제 호출을 검증한다.")
    @Test
    void removeActiveQueueByUuidShouldProveDeleteActiveTokenByUuid() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when
        waitingQueueCommandService.removeActiveQueueByUuid(uuid);

        // then
        verify(waitingQueueWriter).removeActiveTokenByUuid(uuid);
    }

    @DisplayName("대기열 토큰 삭제 호출을 검증한다.")
    @Test
    void removeActiveQueueShouldProveDeleteActiveToken() {
        // when
        waitingQueueCommandService.removeExpiredActiveTokens();

        // then
        verify(waitingQueueWriter).removeExpiredTokens(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

}