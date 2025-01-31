package com.server.concert_reservation.application.payment;

import com.server.concert_reservation.api_backup.user.application.UserCommandService;
import com.server.concert_reservation.api_backup.user.application.UserQueryService;
import com.server.concert_reservation.api_backup.user.application.dto.UserCommand;
import com.server.concert_reservation.application.payment.dto.PaymentCommand;
import com.server.concert_reservation.application.payment.dto.PaymentResult;
import com.server.concert_reservation.domain.concert.service.ConcertCommandService;
import com.server.concert_reservation.domain.concert.service.ConcertQueryService;
import com.server.concert_reservation.domain.payment.service.PaymentService;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenCommandService;
import com.server.concert_reservation.support.api.common.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentUseCase {

    private final PaymentService paymentService;
    private final ConcertQueryService concertQueryService;
    private final ConcertCommandService concertCommandService;

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final QueueTokenCommandService tokenCommandService;

    @DistributedLock(prefix = "reservation", key = "#command.reservationId", waitTime = 1000)
    public PaymentResult payment(PaymentCommand command) {
        val user = userQueryService.getUser(command.userId());
        val reservation = concertQueryService.findTemporaryReservation(command.reservationId());
        val wallet = userCommandService.usePoint(UserCommand.from(user.id(), reservation.totalPrice()));
        val payment = paymentService.paymentReservation(wallet.userId(), command.reservationId(), reservation.totalPrice());

        concertCommandService.completeReservation(reservation.id());
        tokenCommandService.expireToken(command.token());

        return PaymentResult.from(payment);
    }
}
