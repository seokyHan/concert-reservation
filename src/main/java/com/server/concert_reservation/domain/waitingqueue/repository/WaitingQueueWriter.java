package com.server.concert_reservation.domain.waitingqueue.repository;

public interface WaitingQueueWriter {

    boolean addWaitingQueue(String uuid, Long score);

    void moveToActiveQueue(String uuid, Long expirationTimestamp);

    void removeActiveTokenByUuid(String uuid);

    void removeExpiredTokens(Long currentTimestamp);

}
