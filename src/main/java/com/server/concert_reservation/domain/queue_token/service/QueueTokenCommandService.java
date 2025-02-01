package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class QueueTokenCommandService {

    private final QueueTokenReader tokenReader;
    private final QueueTokenWriter tokenWriter;
    private final TimeManager timeManager;
    private final UUIDManager uuidManager;

    public QueueTokenInfo createToken(Long userId) {
        val queueToken = QueueToken.builder()
                .userId(userId)
                .token(uuidManager.generateUuid())
                .status(QueueTokenStatus.WAITING)
                .build();

        return QueueTokenInfo.from(tokenWriter.save(queueToken));
    }

    public void checkActivatedToken(String token) {
        val queueToke = tokenReader.getByToken(token);
        queueToke.validateToken();
    }

    public void activateToken(String token) {
        val currentWaitingToken = tokenReader.getByToken(token);
        currentWaitingToken.activate(timeManager.now());
        tokenWriter.save(currentWaitingToken);
    }

    public void expireToken(String token) {
        val currentWaitingToken = tokenReader.getByToken(token);
        currentWaitingToken.expire(timeManager.now());
        tokenWriter.save(currentWaitingToken);
    }
}
