package com.server.concert_reservation.application.queue_token;

import com.server.concert_reservation.application.queue_token.dto.QueueTokenResult;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenCommandService;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueTokenSchedulerUseCase {

    private final QueueTokenCommandService queueTokenCommandService;
    private final QueueTokenQueryService queueTokenQueryService;

    public List<QueueTokenResult> getWaitingToken(int activeCount) {
        val queueToken = queueTokenQueryService.findWaitingToken(activeCount);

        return queueToken.stream()
                .map(QueueTokenResult::from)
                .collect(Collectors.toList());
    }

    public List<QueueTokenResult> getWaitingTokenToBeExpired(int minutes) {
        val queueToken = queueTokenQueryService.findWaitingTokensToBeExpired(minutes);

        return queueToken.stream()
                .map(QueueTokenResult::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void activateToken(String token) {
        queueTokenCommandService.activateToken(token);
    }

    @Transactional
    public void expireToken(String token) {
        queueTokenCommandService.expireToken(token);
    }
}
