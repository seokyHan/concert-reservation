package com.server.concert_reservation.application.payment;

import com.server.concert_reservation.application.payment.dto.PaymentCommand;
import com.server.concert_reservation.application.payment.dto.PaymentResult;
import com.server.concert_reservation.domain.concert.service.ConcertCommandService;
import com.server.concert_reservation.domain.concert.service.ConcertQueryService;
import com.server.concert_reservation.domain.payment.service.PaymentService;
import com.server.concert_reservation.domain.user.UserCommandService;
import com.server.concert_reservation.domain.user.UserQueryService;
import com.server.concert_reservation.domain.waitingqueue.service.WaitingQueueCommandService;
import com.server.concert_reservation.interfaces.web.support.aspect.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {

    private final PaymentService paymentService;
    private final ConcertQueryService concertQueryService;
    private final ConcertCommandService concertCommandService;

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final WaitingQueueCommandService tokenCommandService;

    @DistributedLock(prefix = "reservation", key = "#command.reservationId", waitTime = 1000)
    @Transactional
    public PaymentResult payment(PaymentCommand command) {
        val user = userQueryService.findUser(command.userId());
        val reservation = concertQueryService.findTemporaryReservation(command.reservationId());
        val wallet = userCommandService.usePoint(user.id(), reservation.totalPrice());
        val payment = paymentService.paymentReservation(wallet.userId(), command.reservationId(), reservation.totalPrice());

        concertCommandService.completeReservation(reservation.id());
        tokenCommandService.removeActiveQueueByUuid(command.token());

        return PaymentResult.from(payment);
    }
}
