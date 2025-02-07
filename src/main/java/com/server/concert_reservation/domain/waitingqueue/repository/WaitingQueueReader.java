package com.server.concert_reservation.domain.waitingqueue.repository;


import java.util.Set;

public interface WaitingQueueReader {

    Long findRankInWaitingQueue(String uuid);

    Long findRankInActiveQueue(String uuid);

    Double findScoreInActiveQueue(String uuid);

    Set<Object> getWaitingQueue(int count);

    Set<Object> findAllActiveTokens();
}
