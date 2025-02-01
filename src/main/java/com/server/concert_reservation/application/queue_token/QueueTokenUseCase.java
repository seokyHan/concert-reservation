package com.server.concert_reservation.application.queue_token;

import com.server.concert_reservation.api_backup.user.application.UserQueryService;
import com.server.concert_reservation.application.queue_token.dto.QueueTokenCommand;
import com.server.concert_reservation.application.queue_token.dto.QueueTokenResult;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenCommandService;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenQueryService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueTokenUseCase {

    private final UserQueryService userQueryService;
    private final QueueTokenCommandService queueTokenCommandService;
    private final QueueTokenQueryService queueTokenQueryService;

    @Transactional
    public QueueTokenResult issueToken(QueueTokenCommand command) {
        val user = userQueryService.getUser(command.userId());
        val issuedToken = queueTokenCommandService.createToken(user.id());
        val waitingIssuedToken = queueTokenQueryService.findWaitingToken(issuedToken.token(), issuedToken.userId());

        return QueueTokenResult.from(waitingIssuedToken);
    }

    public QueueTokenResult getWaitingToken(String token, Long userId) {
        val user = userQueryService.getUser(userId);
        val queueToken = queueTokenQueryService.findQueueToken(token);
        val waitingToken = queueTokenQueryService.findWaitingToken(queueToken.token(), user.id());

        return QueueTokenResult.from(waitingToken);
    }
}
