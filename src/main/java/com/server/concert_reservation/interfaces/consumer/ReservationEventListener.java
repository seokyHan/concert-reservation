package com.server.concert_reservation.interfaces.consumer;

import com.server.concert_reservation.domain.send.DataPlatformSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final DataPlatformSendService dataPlatformSendService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void consumeDataPlatFormSendEvent(Object event) {
        dataPlatformSendService.send(event);
    }
}
