package com.server.concert_reservation.domain.waitingqueue.repository;

import java.util.concurrent.TimeUnit;

public interface WaitingQueueWriter {

    String saveWaitingQueue(String uuid);

    void activateWaitingQueues(int availableSlots, long timeout, TimeUnit unit);

    void deleteActiveTokenByUuid(String uuid);

    void deleteActiveToken();

}
