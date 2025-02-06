package com.server.concert_reservation.domain.waitingqueue.repository;


import com.server.concert_reservation.domain.waitingqueue.dto.WaitingQueueInfo;

import java.util.List;

public interface WaitingQueueReader {

    Long findWaitingQueuePosition(String uuid);

    WaitingQueueInfo findActiveToken(String uuid);

    List<WaitingQueueInfo> findAllActiveTokens();
}
