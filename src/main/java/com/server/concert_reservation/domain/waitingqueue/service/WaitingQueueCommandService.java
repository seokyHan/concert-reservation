package com.server.concert_reservation.domain.waitingqueue.service;

import com.server.concert_reservation.domain.waitingqueue.repository.WaitingQueueWriter;
import com.server.concert_reservation.interfaces.web.support.uuid.UUIDManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class WaitingQueueCommandService {

    private final WaitingQueueWriter waitingQueueWriter;
    private final UUIDManager uuidManager;

    public String createWaitingToken() {
        val uuid = uuidManager.generateUuid();
        val isAdded = waitingQueueWriter.addWaitingQueue(uuid, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        return isAdded ? uuid : null;
    }

    public void activateWaitingQueue(Set<Object> waitingQueue, int timeout) {
        waitingQueue.stream()
                .map(Object::toString)
                .forEach(uuid -> {
                    val expirationTimestamp = LocalDateTime.now()
                            .plus(timeout, TimeUnit.MINUTES.toChronoUnit())
                            .toEpochSecond(ZoneOffset.UTC);
                    waitingQueueWriter.moveToActiveQueue(uuid, expirationTimestamp);
                });
    }

    public void removeActiveQueueByUuid(String uuid) {
        waitingQueueWriter.removeActiveTokenByUuid(uuid);
    }

    public void removeExpiredActiveTokens() {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        waitingQueueWriter.removeExpiredTokens(now);
    }

}
