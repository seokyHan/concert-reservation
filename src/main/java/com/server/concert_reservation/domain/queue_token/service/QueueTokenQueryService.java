package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueTokenQueryService {

    private final QueueTokenReader queueTokenReader;

    public QueueTokenInfo findWaitingToken(String token, Long userId) {
        val waitingToken = queueTokenReader.getByUserIdAndToken(userId, token);
        if (!waitingToken.isWaiting()) {
            return QueueTokenInfo.of(waitingToken, 0L);
        }

        val waitingOrder = queueTokenReader.getLatestActiveToken()
                .map(latestActivatedToken -> waitingToken.getId() - latestActivatedToken.getId())
                .orElse(waitingToken.getId());

        return QueueTokenInfo.of(waitingToken, waitingOrder);
    }

    public List<QueueTokenInfo> findWaitingToken(int activeCount) {
        val queueToken = queueTokenReader.getWaitingToken(activeCount);

        return queueToken.stream()
                .map(QueueTokenInfo::from)
                .collect(Collectors.toList());
    }

    public List<QueueTokenInfo> findWaitingTokensToBeExpired(int minutes) {
        val queueToken = queueTokenReader.getWaitingTokensToBeExpired(minutes);

        return queueToken.stream()
                .map(QueueTokenInfo::from)
                .collect(Collectors.toList());
    }

    public QueueTokenInfo findQueueToken(String token) {
        val queueToken = queueTokenReader.getByToken(token);
        return QueueTokenInfo.from(queueToken);
    }


}
